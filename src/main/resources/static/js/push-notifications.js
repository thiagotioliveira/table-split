/**
 * TableSplit Push Notifications Manager
 */
const PushNotifications = {
    // Utility to convert VAPID public key
    urlBase64ToUint8Array(base64String) {
        const padding = '='.repeat((4 - base64String.length % 4) % 4);
        const base64 = (base64String + padding)
            .replace(/\-/g, '+')
            .replace(/_/g, '/');

        const rawData = window.atob(base64);
        const outputArray = new Uint8Array(rawData.length);

        for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
        }
        return outputArray;
    },

    async isSupported() {
        return 'serviceWorker' in navigator && 'PushManager' in window;
    },

    async getSubscription() {
        const registration = await navigator.serviceWorker.ready;
        return await registration.pushManager.getSubscription();
    },

    async subscribe() {
        try {
            // Explicitly request permission first to ensure user gesture context is not lost
            const permission = await Notification.requestPermission();
            if (permission !== 'granted') {
                console.warn('Push notification permission denied:', permission);
                if (permission === 'denied') {
                    alert('As notificações foram bloqueadas. Por favor, ative-as nas configurações do seu navegador ou sistema.');
                }
                return false;
            }

            const registration = await navigator.serviceWorker.ready;
            
            // Get public key from server
            const response = await fetch("/api/notifications/public-key");
            if (!response.ok) throw new Error("Failed to fetch VAPID public key");
            
            const vapidPublicKey = await response.text();
            if (!vapidPublicKey || vapidPublicKey.startsWith("<")) {
                throw new Error("Invalid VAPID public key received (likely an error page)");
            }
            
            const convertedVapidKey = this.urlBase64ToUint8Array(vapidPublicKey);

            const subscription = await registration.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: convertedVapidKey
            });

            // Send subscription to server
            await this.saveSubscription(subscription);
            
            console.log('User is subscribed to Push Notifications');
            return true;
        } catch (error) {
            console.error('Failed to subscribe the user: ', error);
            // On iOS/Standalone PWA, errors here often mean the user needs to interact more 
            // or the manifest is missing properties.
            alert('Erro ao ativar notificações: ' + error.message);
            return false;
        }
    },

    async unsubscribe() {
        try {
            const subscription = await this.getSubscription();
            if (subscription) {
                await subscription.unsubscribe();
                
                // Notify server
                await fetch('/api/notifications/unsubscribe', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(subscription.endpoint)
                });
            }
            return true;
        } catch (error) {
            console.error('Error unsubscribing', error);
            return false;
        }
    },

    async saveSubscription(subscription) {
        const key = subscription.getKey('p256dh');
        const token = subscription.getKey('auth');
        
        const data = {
            endpoint: subscription.endpoint,
            p256dh: btoa(String.fromCharCode.apply(null, new Uint8Array(key))),
            auth: btoa(String.fromCharCode.apply(null, new Uint8Array(token)))
        };

        await fetch('/api/notifications/subscribe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
    },

    async sendTest() {
        const response = await fetch('/api/notifications/test', { method: 'POST' });
        if (!response.ok) {
            throw new Error('Erro ao enviar comando de teste');
        }
    }
};
