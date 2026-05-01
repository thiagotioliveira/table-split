/**
 * ordersApi Bridge
 *
 * AUTO-GENERATED — do NOT edit manually.
 * Source: order-v1.yaml
 * Regenerated on every Maven build.
 *
 * Uses native browser fetch (ES6 module, no Node.js dependencies).
 */

const BASE_PATH = '/api/v1/manager/orders';

function _csrfHeaders() {
    const token  = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    return (token && header) ? { [header]: token } : {};
}

function _headers() {
    return {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
        ..._csrfHeaders(),
    };
}

async function request(method, path, body, varName) {
    const opts = { method, headers: _headers() };
    if (body !== undefined) opts.body = JSON.stringify(body);
    const res = await fetch(BASE_PATH + path, opts);
    if (!res.ok) {
        const text = await res.text().catch(() => res.statusText);
        throw new Error(`[${varName}] ${method} ${path} → ${res.status}: ${text}`);
    }
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json') && res.status !== 204) return res.json();
    return null;
}

const ordersApi = {
    /** GET /history */
    getOrderHistory(start, end) {
        const _p = new URLSearchParams();
        if (start !== undefined && start !== null) _p.append('start', start);
        if (end !== undefined && end !== null) _p.append('end', end);
        const _qs = _p.toString() ? '?' + _p.toString() : '';
        return request('GET', `/history` + _qs, 'ordersApi');
    },
    /** POST /move */
    moveTicket(body) {
        return request('POST', `/move`, body, 'ordersApi');
    },
    /** GET /{id} */
    getTicketById(id) {
        return request('GET', `/${id}`, 'ordersApi');
    },
    /** GET /count/pending */
    getPendingCount() {
        return request('GET', `/count/pending`, 'ordersApi');
    },
    /** POST /item/cancel */
    cancelTicketItem(body) {
        return request('POST', `/item/cancel`, body, 'ordersApi');
    },
    /** POST /tables/{tableId}/place */
    placeOrder(tableId, body) {
        return request('POST', `/tables/${tableId}/place`, body, 'ordersApi');
    },
    /** POST /items/{itemId}/status */
    updateItemStatus(itemId, body) {
        return request('POST', `/items/${itemId}/status`, body, 'ordersApi');
    },
};

export { ordersApi };
