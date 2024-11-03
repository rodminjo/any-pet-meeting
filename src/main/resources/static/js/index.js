const wrppaer = document.querySelector('.wrapper');

const loginLink = document.querySelector('.login-link');

const registerLink = document.querySelector('.register-link');

const popupBtn = document.querySelector('.btnLogin-popup');

const closeBtn = document.querySelector('.icon-close');

registerLink.addEventListener('click', () => {
  wrppaer.classList.add('active');
});

loginLink.addEventListener('click', () => {
  wrppaer.classList.remove('active');
});

popupBtn.addEventListener('click', () => {
  wrppaer.classList.add('active-popup');
});
closeBtn.addEventListener('click', () => {
  wrppaer.classList.remove('active-popup');
});
