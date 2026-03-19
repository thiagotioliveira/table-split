/**
 * TableSplit - Loading Overlay
 *
 * Uso:
 *   showLoading()           - Exibe overlay com texto padrão "Carregando"
 *   showLoading('Salvando') - Exibe overlay com texto customizado
 *   hideLoading()           - Esconde o overlay
 *
 * O overlay é criado automaticamente na primeira chamada.
 */

(function() {
    'use strict';

    let loadingOverlay = null;
    let loadingTextEl = null;

    // Textos padrão por idioma (detecta pelo html lang ou localStorage)
    const defaultTexts = {
        'pt-BR': 'Carregando',
        'pt': 'Carregando',
        'en-US': 'Loading',
        'en': 'Loading',
        'es-ES': 'Cargando',
        'es': 'Cargando'
    };

    function getDefaultText() {
        const lang = document.documentElement.lang || localStorage.getItem('lang') || 'pt-BR';
        return defaultTexts[lang] || defaultTexts['pt-BR'];
    }

    function createOverlay() {
        if (loadingOverlay) return;

        loadingOverlay = document.createElement('div');
        loadingOverlay.className = 'loading-overlay';
        loadingOverlay.id = 'loadingOverlay';
        loadingOverlay.innerHTML = `
      <div class="loading-spinner">
        <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2"/>
          <path d="M7 2v20"/>
          <path d="M21 15V2a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7"/>
        </svg>
      </div>
      <div class="loading-text">
        <span id="loadingText">${getDefaultText()}</span>
        <span class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </span>
      </div>
    `;

        document.body.appendChild(loadingOverlay);
        loadingTextEl = document.getElementById('loadingText');
    }

    // Função global para mostrar loading
    window.showLoading = function(text) {
        createOverlay();

        if (text) {
            loadingTextEl.textContent = text;
        } else {
            loadingTextEl.textContent = getDefaultText();
        }

        loadingOverlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    };

    // Função global para esconder loading
    window.hideLoading = function() {
        if (loadingOverlay) {
            loadingOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    // Atalho para simular loading em chamadas async
    window.withLoading = async function(asyncFn, text) {
        showLoading(text);
        try {
            return await asyncFn();
        } finally {
            hideLoading();
        }
    };

})();