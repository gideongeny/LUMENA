import './style.css'

// Scroll Reveal Animation
const revealElements = document.querySelectorAll('.reveal');

const revealObserver = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
    }
  });
}, {
  threshold: 0.1,
  rootMargin: "0px 0px -50px 0px"
});

revealElements.forEach(el => revealObserver.observe(el));

// Hero Card 3D Tilt Effect
const heroSection = document.querySelector('.hero') as HTMLElement;
const phoneMockup = document.querySelector('.phone-mockup') as HTMLElement;

if (heroSection && phoneMockup) {
  heroSection.addEventListener('mousemove', (e) => {
    const { offsetWidth: width, offsetHeight: height } = heroSection;
    const { clientX: x, clientY: y } = e;

    const xPos = (x / width - 0.5);
    const yPos = (y / height - 0.5);

    // Limit rotation to small angles
    const rotateY = xPos * 20; // max 10 deg
    const rotateX = yPos * -20; // max 10 deg

    phoneMockup.style.transform = `perspective(1000px) rotateY(${rotateY}deg) rotateX(${rotateX}deg)`;
  });

  heroSection.addEventListener('mouseleave', () => {
    phoneMockup.style.transform = `perspective(1000px) rotateY(0deg) rotateX(0deg)`;
    phoneMockup.style.transition = 'transform 0.5s ease-out';
  });

  heroSection.addEventListener('mouseenter', () => {
    phoneMockup.style.transition = 'none';
  });
}
