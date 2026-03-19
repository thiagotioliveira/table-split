// TableSplit PWA Installation Helper
// Inclua este script nas páginas onde deseja mostrar o prompt de instalação

(function() {
  'use strict';
  
  // Guarda o evento de instalação
  let deferredPrompt = null;
  
  // Registra o Service Worker
  async function registerServiceWorker() {
    if (!('serviceWorker' in navigator)) {
      console.log('[PWA] Service Worker não suportado');
      return false;
    }
    
    try {
      const registration = await navigator.serviceWorker.register('/sw.js', {
        scope: '/'
      });
      
      console.log('[PWA] Service Worker registrado:', registration.scope);
      
      // Verifica atualizações
      registration.addEventListener('updatefound', () => {
        const newWorker = registration.installing;
        console.log('[PWA] Novo Service Worker encontrado');
        
        newWorker.addEventListener('statechange', () => {
          if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
            // Nova versão disponível
            showUpdateNotification();
          }
        });
      });
      
      return true;
    } catch (error) {
      console.error('[PWA] Erro ao registrar Service Worker:', error);
      return false;
    }
  }
  
  // Captura evento de instalação
  window.addEventListener('beforeinstallprompt', (event) => {
    console.log('[PWA] beforeinstallprompt disparado');
    event.preventDefault();
    deferredPrompt = event;
    
    // Mostra botão/banner de instalação customizado
    showInstallButton();
  });
  
  // Detecta quando o app foi instalado
  window.addEventListener('appinstalled', () => {
    console.log('[PWA] App instalado com sucesso');
    deferredPrompt = null;
    hideInstallButton();
    
    // Analytics ou notificação
    if (typeof gtag === 'function') {
      gtag('event', 'pwa_install', { event_category: 'PWA' });
    }
  });
  
  // Mostra botão de instalação
  function showInstallButton() {
    const existingBtn = document.getElementById('pwa-install-btn');
    if (existingBtn) {
      existingBtn.style.display = 'flex';
      return;
    }
    
    // Cria botão flutuante de instalação
    const btn = document.createElement('button');
    btn.id = 'pwa-install-btn';
    btn.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
      </svg>
      <span>Instalar App</span>
    `;
    btn.style.cssText = `
      position: fixed;
      bottom: 20px;
      right: 20px;
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
      color: white;
      font-family: 'Inter', sans-serif;
      font-weight: 600;
      font-size: 14px;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4);
      z-index: 10000;
      transition: transform 0.2s, box-shadow 0.2s;
    `;
    
    btn.addEventListener('mouseenter', () => {
      btn.style.transform = 'translateY(-2px)';
      btn.style.boxShadow = '0 8px 25px rgba(249, 115, 22, 0.5)';
    });
    
    btn.addEventListener('mouseleave', () => {
      btn.style.transform = 'translateY(0)';
      btn.style.boxShadow = '0 4px 15px rgba(249, 115, 22, 0.4)';
    });
    
    btn.addEventListener('click', promptInstall);
    
    document.body.appendChild(btn);
  }
  
  // Esconde botão de instalação
  function hideInstallButton() {
    const btn = document.getElementById('pwa-install-btn');
    if (btn) {
      btn.style.display = 'none';
    }
  }
  
  // Dispara prompt de instalação
  async function promptInstall() {
    if (!deferredPrompt) {
      console.log('[PWA] Prompt de instalação não disponível');
      return false;
    }
    
    deferredPrompt.prompt();
    
    const { outcome } = await deferredPrompt.userChoice;
    console.log('[PWA] Escolha do usuário:', outcome);
    
    deferredPrompt = null;
    hideInstallButton();
    
    return outcome === 'accepted';
  }
  
  // Mostra notificação de atualização
  function showUpdateNotification() {
    const notification = document.createElement('div');
    notification.id = 'pwa-update-notification';
    notification.innerHTML = `
      <div style="display: flex; align-items: center; gap: 12px;">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        <span>Nova versão disponível!</span>
      </div>
      <button onclick="location.reload()" style="
        padding: 8px 16px;
        background: white;
        color: #1a1a2e;
        font-weight: 600;
        border: none;
        border-radius: 8px;
        cursor: pointer;
      ">Atualizar</button>
    `;
    notification.style.cssText = `
      position: fixed;
      bottom: 20px;
      left: 20px;
      right: 20px;
      max-width: 400px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      padding: 16px 20px;
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
      color: white;
      font-family: 'Inter', sans-serif;
      font-size: 14px;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
      z-index: 10001;
    `;
    
    document.body.appendChild(notification);
  }
  
  // Verifica se está rodando como PWA instalado
  function isPWAInstalled() {
    return window.matchMedia('(display-mode: standalone)').matches ||
           window.navigator.standalone === true ||
           document.referrer.includes('android-app://');
  }
  
  // Expõe funções globalmente
  window.TableSplitPWA = {
    install: promptInstall,
    isInstalled: isPWAInstalled,
    registerServiceWorker: registerServiceWorker
  };
  
  // Auto-registra service worker quando DOM estiver pronto
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', registerServiceWorker);
  } else {
    registerServiceWorker();
  }
  
})();
