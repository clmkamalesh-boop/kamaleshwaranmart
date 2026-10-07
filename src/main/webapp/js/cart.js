/**
 * KamaleshwaranMart: Shopping Cart Dynamic UI & Quantity Controls
 */

document.addEventListener('DOMContentLoaded', () => {
    const contextPath = window.CONTEXT_PATH || '';

    function showToast(message, type = 'success') {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `<span>${icons[type] || '📢'}</span><span>${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'fadeOut 0.4s ease forwards';
            setTimeout(() => toast.remove(), 400);
        }, 3000);
    }

    function formatCurrency(amount) {
        return '₹' + parseFloat(amount).toLocaleString('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }

    function updateSummary(totalAmount, count) {
        const subtotal = parseFloat(totalAmount) || 0;
        const tax = subtotal * 0.18;
        const total = subtotal + tax;

        const subtotalElem = document.getElementById('summarySubtotal');
        if (subtotalElem) subtotalElem.textContent = formatCurrency(subtotal);

        const taxElem = document.getElementById('summaryTax');
        if (taxElem) taxElem.textContent = formatCurrency(tax);

        const totalElem = document.getElementById('summaryTotal');
        if (totalElem) totalElem.textContent = formatCurrency(total);

        const countElem = document.getElementById('summaryCount');
        if (countElem && count !== undefined) countElem.textContent = count;
    }

    async function sendCartUpdate(cartItemId, quantity) {
        try {
            const formData = new URLSearchParams();
            formData.append('action', 'update');
            formData.append('cartItemId', cartItemId);
            formData.append('quantity', quantity);

            const response = await fetch(`${contextPath}/cart`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            });

            const res = await response.json();
            if (res.success) {
                const qtyValElem = document.getElementById(`qty-${cartItemId}`);
                if (qtyValElem) qtyValElem.textContent = quantity;

                if (res.data && res.data.cartTotal !== undefined) {
                    updateSummary(res.data.cartTotal, res.data.cartCount);
                }
            } else {
                showToast(res.error ? res.error.message : 'Error updating quantity', 'error');
            }
        } catch (e) {
            console.error('Error updating cart:', e);
            showToast('Failed to sync with server', 'error');
        }
    }

    async function sendCartRemove(cartItemId, itemElem) {
        try {
            const formData = new URLSearchParams();
            formData.append('action', 'remove');
            formData.append('cartItemId', cartItemId);

            const response = await fetch(`${contextPath}/cart`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            });

            const res = await response.json();
            if (res.success) {
                if (itemElem) {
                    itemElem.style.transition = 'all 0.3s ease';
                    itemElem.style.opacity = '0';
                    itemElem.style.transform = 'translateX(20px)';
                    setTimeout(() => {
                        itemElem.remove();
                        const remaining = document.querySelectorAll('.cart-item');
                        if (remaining.length === 0) {
                            window.location.reload();
                        } else if (res.data && res.data.cartTotal !== undefined) {
                            updateSummary(res.data.cartTotal, remaining.length);
                        }
                    }, 300);
                }
                showToast('Item removed from cart', 'success');
            } else {
                showToast(res.error ? res.error.message : 'Error removing item', 'error');
            }
        } catch (e) {
            console.error('Error removing item:', e);
            showToast('Failed to remove item', 'error');
        }
    }

    // Minus button handler
    document.querySelectorAll('.btn-cart-minus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = btn.dataset.itemId;
            const qtyElem = document.getElementById(`qty-${itemId}`);
            let currentQty = parseInt(qtyElem ? qtyElem.textContent : '1', 10);
            if (currentQty > 1) {
                sendCartUpdate(itemId, currentQty - 1);
            }
        });
    });

    // Plus button handler
    document.querySelectorAll('.btn-cart-plus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = btn.dataset.itemId;
            const maxStock = parseInt(btn.dataset.max || '99', 10);
            const qtyElem = document.getElementById(`qty-${itemId}`);
            let currentQty = parseInt(qtyElem ? qtyElem.textContent : '1', 10);
            if (currentQty < maxStock) {
                sendCartUpdate(itemId, currentQty + 1);
            } else {
                showToast(`Maximum stock limit (${maxStock}) reached`, 'warning');
            }
        });
    });

    // Remove button handler
    document.querySelectorAll('.btn-cart-remove').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const itemId = btn.dataset.itemId;
            const itemElem = btn.closest('.cart-item');
            if (confirm('Are you sure you want to remove this item from your cart?')) {
                sendCartRemove(itemId, itemElem);
            }
        });
    });

    // Clear cart handler
    const clearBtn = document.getElementById('clearCartBtn');
    if (clearBtn) {
        clearBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            if (!confirm('Are you sure you want to clear your entire cart?')) return;

            try {
                const formData = new URLSearchParams();
                formData.append('action', 'clear');

                const response = await fetch(`${contextPath}/cart`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: formData.toString()
                });

                const res = await response.json();
                if (res.success) {
                    window.location.reload();
                } else {
                    showToast(res.error ? res.error.message : 'Could not clear cart', 'error');
                }
            } catch (err) {
                showToast('Failed to clear cart', 'error');
            }
        });
    }
});
