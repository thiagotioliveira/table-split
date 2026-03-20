// TableSplit Service Worker
const CACHE_NAME = 'tablesplit-v1';
const STATIC_CACHE = 'tablesplit-static-v1';
const DYNAMIC_CACHE = 'tablesplit-dynamic-v1';

// Arquivo CRÍTICO - sempre deve ser cacheado
const OFFLINE_PAGE = '/offline.html';

// Arquivos estáticos para cache (opcional - falha individual não quebra SW)
const STATIC_ASSETS = [
  '/',
  '/css/style.css',
  '/js/app.js',
  '/images/icons/icon-192x192.png',
  '/images/icons/icon-512x512.png'
];

// Páginas para cache dinâmico
const CACHE_PAGES = [
  '/dashboard',
  '/menu',
  '/orders',
  '/settings',
  '/profile'
];

// Instalação - cacheia arquivos estáticos
self.addEventListener('install', (event) => {
  console.log('[SW] Installing Service Worker...');
  
  event.waitUntil(
    caches.open(STATIC_CACHE)
      .then(async (cache) => {
        // CRÍTICO: Cachear offline.html primeiro e separadamente
        try {
          const offlineResponse = await fetch(OFFLINE_PAGE);
          if (offlineResponse.ok) {
            await cache.put(OFFLINE_PAGE, offlineResponse);
            console.log('[SW] ✓ offline.html cached successfully');
          } else {
            console.error('[SW] ✗ offline.html not found (status:', offlineResponse.status, ')');
          }
        } catch (error) {
          console.error('[SW] ✗ Failed to cache offline.html:', error);
        }
        
        // Cachear outros assets individualmente (falhas não quebram o SW)
        for (const asset of STATIC_ASSETS) {
          try {
            const response = await fetch(asset);
            if (response.ok) {
              await cache.put(asset, response);
              console.log('[SW] ✓ Cached:', asset);
            } else {
              console.warn('[SW] ✗ Not found:', asset, '(status:', response.status, ')');
            }
          } catch (error) {
            console.warn('[SW] ✗ Failed to cache:', asset);
          }
        }
        
        console.log('[SW] Static assets caching complete');
        return self.skipWaiting();
      })
  );
});

// Ativação - limpa caches antigos
self.addEventListener('activate', (event) => {
  console.log('[SW] Activating Service Worker...');
  
  event.waitUntil(
    caches.keys()
      .then((cacheNames) => {
        return Promise.all(
          cacheNames
            .filter((name) => name !== STATIC_CACHE && name !== DYNAMIC_CACHE)
            .map((name) => {
              console.log('[SW] Deleting old cache:', name);
              return caches.delete(name);
            })
        );
      })
      .then(() => {
        console.log('[SW] Service Worker activated');
        return self.clients.claim();
      })
  );
});

// Estratégia de fetch: Network First com fallback para cache
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);
  
  // Ignorar requests que não são GET
  if (request.method !== 'GET') {
    return;
  }
  
  // Ignorar requests externos
  if (url.origin !== location.origin) {
    return;
  }
  
  // Arquivos estáticos (CSS, JS, imagens): Cache First
  if (isStaticAsset(request.url)) {
    event.respondWith(cacheFirst(request));
    return;
  }
  
  // Páginas HTML: Network First
  if (request.headers.get('accept')?.includes('text/html')) {
    event.respondWith(networkFirst(request));
    return;
  }
  
  // API calls: Network Only (não cachear dados dinâmicos)
  if (url.pathname.startsWith('/api/')) {
    event.respondWith(networkOnly(request));
    return;
  }
  
  // Default: Network First
  event.respondWith(networkFirst(request));
});

// Verifica se é arquivo estático
function isStaticAsset(url) {
  const staticExtensions = ['.css', '.js', '.png', '.jpg', '.jpeg', '.gif', '.svg', '.woff', '.woff2', '.ttf'];
  return staticExtensions.some(ext => url.includes(ext));
}

// Estratégia: Cache First (para estáticos)
async function cacheFirst(request) {
  const cachedResponse = await caches.match(request);
  
  if (cachedResponse) {
    // Atualiza cache em background
    updateCache(request);
    return cachedResponse;
  }
  
  try {
    const networkResponse = await fetch(request);
    await addToCache(STATIC_CACHE, request, networkResponse.clone());
    return networkResponse;
  } catch (error) {
    console.error('[SW] Cache First failed:', error);
    return new Response('Offline', { status: 503 });
  }
}

// Estratégia: Network First (para páginas)
async function networkFirst(request) {
  try {
    const networkResponse = await fetch(request);
    
    // Cacheia resposta bem sucedida
    if (networkResponse.ok) {
      await addToCache(DYNAMIC_CACHE, request, networkResponse.clone());
    }
    
    return networkResponse;
  } catch (error) {
    console.log('[SW] Network failed, trying cache for:', request.url);
    
    const cachedResponse = await caches.match(request);
    if (cachedResponse) {
      console.log('[SW] ✓ Serving from cache:', request.url);
      return cachedResponse;
    }
    
    // Retorna página offline se disponível
    console.log('[SW] No cache found, trying offline page...');
    const offlinePage = await caches.match(OFFLINE_PAGE);
    if (offlinePage) {
      console.log('[SW] ✓ Serving offline.html');
      return offlinePage;
    }
    
    console.error('[SW] ✗ offline.html not in cache!');
    return new Response(`
      <!DOCTYPE html>
      <html lang="pt-BR">
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Offline - TableSplit</title>
        <style>
          body { font-family: system-ui, sans-serif; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; background: #1e293b; color: white; text-align: center; }
          h1 { font-size: 1.5rem; margin-bottom: 0.5rem; }
          p { color: #94a3b8; }
        </style>
      </head>
      <body>
        <div>
          <h1>Você está offline</h1>
          <p>Verifique sua conexão e tente novamente.</p>
        </div>
      </body>
      </html>
    `, { 
      status: 503,
      headers: { 'Content-Type': 'text/html; charset=utf-8' }
    });
  }
}

// Estratégia: Network Only (para API)
async function networkOnly(request) {
  try {
    return await fetch(request);
  } catch (error) {
    return new Response(JSON.stringify({ error: 'Offline' }), {
      status: 503,
      headers: { 'Content-Type': 'application/json' }
    });
  }
}

// Adiciona ao cache
async function addToCache(cacheName, request, response) {
  const cache = await caches.open(cacheName);
  await cache.put(request, response);
}

// Atualiza cache em background
async function updateCache(request) {
  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      await addToCache(STATIC_CACHE, request, networkResponse);
    }
  } catch (error) {
    // Falha silenciosa - cache existente continua válido
  }
}

// Push Notifications
self.addEventListener('push', (event) => {
  console.log('[SW] Push notification received');
  
  let data = {
    title: 'TableSplit',
    body: 'Você tem uma nova notificação',
    icon: '/images/icons/icon-192x192.png',
    badge: '/images/icons/icon-72x72.png',
    tag: 'tablesplit-notification'
  };
  
  if (event.data) {
    try {
      data = { ...data, ...event.data.json() };
    } catch (e) {
      data.body = event.data.text();
    }
  }
  
  event.waitUntil(
    self.registration.showNotification(data.title, {
      body: data.body,
      icon: data.icon,
      badge: data.badge,
      tag: data.tag,
      vibrate: [200, 100, 200],
      data: data.url || '/',
      actions: data.actions || []
    })
  );
});

// Click na notificação
self.addEventListener('notificationclick', (event) => {
  console.log('[SW] Notification clicked');
  event.notification.close();
  
  const url = event.notification.data || '/';
  
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true })
      .then((windowClients) => {
        // Foca janela existente se encontrar
        for (const client of windowClients) {
          if (client.url.includes(url) && 'focus' in client) {
            return client.focus();
          }
        }
        // Abre nova janela
        if (clients.openWindow) {
          return clients.openWindow(url);
        }
      })
  );
});

// Background Sync (para sincronizar pedidos offline)
self.addEventListener('sync', (event) => {
  console.log('[SW] Background sync:', event.tag);
  
  if (event.tag === 'sync-orders') {
    event.waitUntil(syncOrders());
  }
});

// Sincroniza pedidos pendentes
async function syncOrders() {
  try {
    // Busca pedidos pendentes do IndexedDB
    const pendingOrders = await getPendingOrders();
    
    for (const order of pendingOrders) {
      try {
        const response = await fetch('/api/orders', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(order)
        });
        
        if (response.ok) {
          await removePendingOrder(order.id);
          console.log('[SW] Order synced:', order.id);
        }
      } catch (error) {
        console.error('[SW] Failed to sync order:', order.id);
      }
    }
  } catch (error) {
    console.error('[SW] Sync failed:', error);
  }
}

// Placeholder functions para IndexedDB
async function getPendingOrders() {
  // Implementar com IndexedDB no seu app
  return [];
}

async function removePendingOrder(id) {
  // Implementar com IndexedDB no seu app
}
