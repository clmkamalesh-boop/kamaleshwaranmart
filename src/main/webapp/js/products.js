/* =====================================================
   KamaleshwaranMart - Products JavaScript
   (Add to Cart, Review submission, Sort auto-submit)
   ===================================================== */

(function () {
  'use strict';

  const CTX = window.CONTEXT_PATH || '';

  /* ===== Toast Notifications ===== */
  function showToast(message, type = 'success', duration = 3500) {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
    const toast  = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type] || '📢'}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.animation = 'fadeOut 0.4s ease forwards';
      setTimeout(() => toast.remove(), 400);
    }, duration);
  }

  /* ===== Add to Cart (Products Grid & Home) ===== */
  document.querySelectorAll('.btn-add-cart').forEach(btn => {
    btn.addEventListener('click', async () => {
      const productId = btn.dataset.productId;
      if (!productId) return;
      await addToCart(productId, 1, btn);
    });
  });

  /* ===== Add to Cart (Product Detail Page) ===== */
  const btnAddToCart = document.getElementById('btnAddToCart');
  const qtyInput     = document.getElementById('qtyInput');

  if (btnAddToCart) {
    btnAddToCart.addEventListener('click', async () => {
      const productId = btnAddToCart.dataset.productId;
      const qty       = parseInt(qtyInput ? qtyInput.value : '1', 10);
      await addToCart(productId, qty, btnAddToCart);
    });
  }

  async function addToCart(productId, quantity, btn) {
    const originalText = btn.textContent;
    btn.disabled    = true;
    btn.textContent = '⏳ Adding...';
    try {
      const res = await fetch(`${CTX}/cart`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
        body: JSON.stringify({ productId: Number(productId), quantity: quantity, action: 'add' }),
      });
      const data = await res.json();
      if (res.ok && data.success) {
        showToast('🛒 Added to cart!', 'success');
        btn.textContent = '✅ Added!';
        setTimeout(() => { btn.textContent = originalText; btn.disabled = false; }, 2000);
      } else {
        showToast(data.message || 'Failed to add to cart. Please sign in.', 'error');
        btn.textContent = originalText;
        btn.disabled    = false;
        if (res.status === 401) setTimeout(() => { window.location.href = `${CTX}/login?error=session`; }, 1500);
      }
    } catch (err) {
      showToast('Network error. Please try again.', 'error');
      btn.textContent = originalText;
      btn.disabled    = false;
    }
  }

  /* ===== Product Detail – Quantity Selector ===== */
  const qtyDown    = document.getElementById('qtyDown');
  const qtyUp      = document.getElementById('qtyUp');
  const qtyDisplay = document.getElementById('qtyDisplay');

  if (qtyDown && qtyUp && qtyDisplay) {
    const maxQty = parseInt(btnAddToCart ? btnAddToCart.closest('[data-stock]')?.dataset.stock ?? 99 : 99);

    qtyDown.addEventListener('click', () => {
      let v = parseInt(qtyDisplay.textContent);
      if (v > 1) { v--; qtyDisplay.textContent = v; if (qtyInput) qtyInput.value = v; }
    });
    qtyUp.addEventListener('click', () => {
      let v = parseInt(qtyDisplay.textContent);
      if (v < maxQty) { v++; qtyDisplay.textContent = v; if (qtyInput) qtyInput.value = v; }
    });
  }

  /* ===== Sort dropdown auto-submit ===== */
  const sortSelect = document.getElementById('sortSelect');
  const filterForm = document.getElementById('filterForm');
  if (sortSelect && filterForm) {
    sortSelect.addEventListener('change', () => filterForm.submit());
  }

  /* ===== Review Form (Product Detail) ===== */
  const reviewForm      = document.getElementById('reviewForm');
  const starPicks       = document.querySelectorAll('.star-pick');
  const reviewRatingInp = document.getElementById('reviewRating');

  // Star click
  starPicks.forEach(star => {
    star.addEventListener('click', () => {
      const val = parseInt(star.dataset.val);
      if (reviewRatingInp) reviewRatingInp.value = val;
      starPicks.forEach((s, i) => { s.textContent = i < val ? '⭐' : '☆'; });
    });
    star.addEventListener('mouseover', () => {
      const val = parseInt(star.dataset.val);
      starPicks.forEach((s, i) => { s.textContent = i < val ? '⭐' : '☆'; });
    });
    star.addEventListener('mouseleave', () => {
      const currentRating = parseInt(reviewRatingInp?.value || '0');
      starPicks.forEach((s, i) => { s.textContent = i < currentRating ? '⭐' : '☆'; });
    });
  });

  if (reviewForm) {
    reviewForm.addEventListener('submit', async e => {
      e.preventDefault();
      const productId = document.getElementById('reviewProductId')?.value;
      const rating    = parseInt(reviewRatingInp?.value || '0');
      const comment   = document.getElementById('reviewText')?.value?.trim();

      if (!rating || rating < 1) { showToast('Please select a rating.', 'warning'); return; }
      if (!comment || comment.length < 5) { showToast('Please write a comment (min 5 chars).', 'warning'); return; }

      try {
        const res = await fetch(`${CTX}/reviews`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
          body: JSON.stringify({ productId: Number(productId), rating, comment }),
        });
        const data = await res.json();
        if (res.ok && data.success) {
          showToast('⭐ Review submitted! Thank you.', 'success');
          reviewForm.reset();
          starPicks.forEach(s => s.textContent = '☆');
          if (reviewRatingInp) reviewRatingInp.value = 0;
          setTimeout(() => window.location.reload(), 1500);
        } else {
          showToast(data.message || 'Failed to submit review.', 'error');
        }
      } catch {
        showToast('Network error. Please try again.', 'error');
      }
    });
  }

})();
