/* =====================================================
   KamaleshwaranMart - Auth JavaScript
   ===================================================== */

(function () {
  'use strict';

  /* ===== Toggle Password Visibility ===== */
  document.querySelectorAll('.toggle-password').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.dataset.target;
      const input = document.getElementById(targetId);
      if (!input) return;
      if (input.type === 'password') {
        input.type = 'text';
        btn.textContent = '🙈';
      } else {
        input.type = 'password';
        btn.textContent = '👁️';
      }
    });
  });

  /* ===== Password Strength Meter ===== */
  const passwordInput  = document.getElementById('password');
  const confirmInput   = document.getElementById('confirmPassword');
  const strengthBar    = document.getElementById('strengthBar');
  const strengthLabel  = document.getElementById('strengthLabel');

  if (passwordInput && strengthBar) {
    passwordInput.addEventListener('input', () => {
      const score = measureStrength(passwordInput.value);
      const styles = [
        { width: '0%',   bg: '',          label: '' },
        { width: '25%',  bg: '#ef4444',   label: 'Weak' },
        { width: '50%',  bg: '#f59e0b',   label: 'Fair' },
        { width: '75%',  bg: '#3b82f6',   label: 'Good' },
        { width: '100%', bg: '#10b981',   label: 'Strong' },
      ];
      const s = styles[score];
      strengthBar.style.width      = s.width;
      strengthBar.style.background = s.bg;
      if (strengthLabel) strengthLabel.textContent = s.label;
    });
  }

  function measureStrength(pw) {
    if (!pw) return 0;
    let score = 0;
    if (pw.length >= 8)         score++;
    if (/[A-Z]/.test(pw))      score++;
    if (/[0-9]/.test(pw))      score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;
    return score;
  }

  /* ===== Form Validation + Submit Loading ===== */
  const form      = document.getElementById('authForm');
  const submitBtn = document.getElementById('submitBtn');

  if (form) {
    form.addEventListener('submit', e => {
      // Password match validation on register page
      if (passwordInput && confirmInput) {
        if (passwordInput.value !== confirmInput.value) {
          e.preventDefault();
          showFieldError(confirmInput, 'Passwords do not match.');
          return;
        }
        if (passwordInput.value.length < 8) {
          e.preventDefault();
          showFieldError(passwordInput, 'Password must be at least 8 characters.');
          return;
        }
      }

      // Loading state
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = '⏳ Please wait...';
      }
    });
  }

  function showFieldError(input, message) {
    removeFieldErrors();
    const err = document.createElement('div');
    err.className = 'alert alert-danger';
    err.style.marginTop = '0.5rem';
    err.style.fontSize  = '0.85rem';
    err.textContent = message;
    input.style.borderColor = '#ef4444';
    input.parentElement.insertAdjacentElement('afterend', err);
    input.focus();
    setTimeout(removeFieldErrors, 4000);
  }

  function removeFieldErrors() {
    document.querySelectorAll('.form-control').forEach(i => i.style.borderColor = '');
    document.querySelectorAll('.field-error').forEach(e => e.remove());
  }

})();
