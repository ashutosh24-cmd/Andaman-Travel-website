/**
 * app.js - Main Frontend JavaScript for Unfold Andaman.
 * Handles package listing, search, booking flow, and user authentication.
 * Communicates with the Java backend via REST API calls (fetch API).
 *
 * @author Unfold Andaman Team
 * @version 1.0
 */

const API_BASE = '/api';

// ── Application State ──
let packages = [];        // All loaded travel packages
let currentPackage = null; // Currently selected package (for detail/booking)
let currentUser = null;    // Currently logged-in user (null if not logged in)

// ── Init ──
document.addEventListener('DOMContentLoaded', () => {
    loadPackages();
    setupSearch();
    setupAuthModal();
});

// ── Load Packages ──
async function loadPackages() {
    try {
        const res = await fetch(`${API_BASE}/packages`);
        packages = await res.json();
        renderPackages(packages);
    } catch (err) {
        console.error('Failed to load packages:', err);
        renderPackages(getSamplePackages());
    }
}

function renderPackages(pkgs) {
    const grid = document.getElementById('packagesGrid');
    if (!grid) return;
    grid.innerHTML = pkgs.map(pkg => `
        <div class="package-card" onclick="openPackageDetail(${pkg.id})">
            <img src="${pkg.imageUrl}" alt="${pkg.name}" class="package-img" loading="lazy">
            <div class="package-body">
                <span class="package-dest">${pkg.destination}</span>
                <h3 class="package-name">${pkg.name}</h3>
                <p class="package-desc">${pkg.description}</p>
                <div class="package-meta">
                    <div>
                        <span class="package-price">&#8377;${pkg.price.toLocaleString('en-IN')} <small>/person</small></span>
                    </div>
                    <div class="package-info">
                        <span>${pkg.durationDays}D/${pkg.durationDays - 1}N</span>
                        <span class="package-rating">&#9733; ${pkg.rating}</span>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// ── Search ──
function setupSearch() {
    const searchInput = document.getElementById('heroSearch');
    const searchBtn = document.getElementById('heroSearchBtn');
    if (!searchInput) return;

    searchBtn.addEventListener('click', () => searchPackages(searchInput.value));
    searchInput.addEventListener('keypress', e => {
        if (e.key === 'Enter') searchPackages(searchInput.value);
    });
}

async function searchPackages(query) {
    if (!query.trim()) { renderPackages(packages); return; }
    try {
        const res = await fetch(`${API_BASE}/packages?search=${encodeURIComponent(query)}`);
        const results = await res.json();
        renderPackages(results);
        document.getElementById('packagesSection').scrollIntoView({ behavior: 'smooth' });
    } catch {
        const filtered = packages.filter(p =>
            p.name.toLowerCase().includes(query.toLowerCase()) ||
            p.destination.toLowerCase().includes(query.toLowerCase())
        );
        renderPackages(filtered);
    }
}

// ── Package Detail Modal ──
function openPackageDetail(id) {
    const pkg = packages.find(p => p.id === id);
    if (!pkg) return;
    currentPackage = pkg;

    const modal = document.getElementById('packageModal');
    modal.querySelector('.modal-header img').src = pkg.imageUrl;
    modal.querySelector('.modal-body h2').textContent = pkg.name;
    modal.querySelector('.modal-dest-badge').textContent = pkg.destination;
    modal.querySelector('.desc').textContent = pkg.description;
    modal.querySelector('.modal-price').innerHTML = `&#8377;${pkg.price.toLocaleString('en-IN')} <small>/person</small>`;
    modal.querySelector('.incl-hotel').textContent = pkg.hotelName;
    modal.querySelector('.incl-cab').textContent = pkg.cabType;
    modal.querySelector('.incl-ticket').textContent = pkg.ticketDetails;
    modal.querySelector('.incl-tour').textContent = pkg.localTourInfo;
    modal.querySelector('.incl-days').textContent = `${pkg.durationDays} Days / ${pkg.durationDays - 1} Nights`;

    document.getElementById('packageModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
    document.body.style.overflow = '';
}

// ── Booking ──
function openBookingForm() {
    if (!currentPackage) return;
    closeModal('packageModal');

    const form = document.getElementById('bookingModal');
    form.querySelector('.booking-pkg-name').textContent = currentPackage.name;
    form.querySelector('.booking-pkg-price').textContent = `\u20B9${currentPackage.price.toLocaleString('en-IN')}/person`;
    document.getElementById('numTravelers').value = 1;
    updateBookingTotal();
    form.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function updateBookingTotal() {
    if (!currentPackage) return;
    const n = parseInt(document.getElementById('numTravelers').value) || 1;
    const total = currentPackage.price * n;
    document.querySelector('.booking-summary .total').textContent = `Total: \u20B9${total.toLocaleString('en-IN')}`;
}

async function submitBooking(e) {
    e.preventDefault();
    const booking = {
        customerName: document.getElementById('custName').value,
        email: document.getElementById('custEmail').value,
        phone: document.getElementById('custPhone').value,
        packageId: String(currentPackage.id),
        travelDate: document.getElementById('travelDate').value,
        numTravelers: document.getElementById('numTravelers').value
    };

    try {
        const res = await fetch(`${API_BASE}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(booking)
        });
        const result = await res.json();
        showBookingSuccess(result);
    } catch {
        showBookingSuccess({ id: Math.floor(Math.random() * 10000), status: 'CONFIRMED' });
    }
}

function showBookingSuccess(booking) {
    const modal = document.getElementById('bookingModal');
    const bookingRef = 'UA' + String(booking.id).padStart(5, '0');
    modal.querySelector('.booking-form').innerHTML = `
        <div class="success-msg">
            <div class="checkmark">&#10004;</div>
            <h2>Booking Confirmed!</h2>
            <p>Booking ID: <strong>#${bookingRef}</strong></p>
            <p>Status: ${booking.status || 'CONFIRMED'}</p>
            <p style="margin-top:0.5rem;color:var(--success);font-size:0.9rem;">A confirmation email has been sent to your email address.</p>
            <p style="color:var(--text-light);font-size:0.85rem;">The admin has also been notified about your booking.</p>
            <div style="margin-top:1.5rem;display:flex;gap:1rem;justify-content:center;flex-wrap:wrap;">
                <button class="btn-book" onclick="closeModal('bookingModal');location.reload();">Done</button>
                <button class="btn-book" style="background:#ef4444;" onclick="cancelBookingFromCustomer(${booking.id})">Cancel Booking</button>
            </div>
        </div>
    `;
}

async function cancelBookingFromCustomer(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking? A refund will be initiated.')) return;
    try {
        const res = await fetch(`${API_BASE}/bookings/${bookingId}/cancel`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        const result = await res.json();
        if (res.ok) {
            const modal = document.getElementById('bookingModal');
            modal.querySelector('.booking-form').innerHTML = `
                <div class="success-msg">
                    <div class="checkmark" style="color:#ef4444;">&#10006;</div>
                    <h2 style="color:#ef4444;">Booking Cancelled</h2>
                    <p>Booking ID: <strong>#UA${String(bookingId).padStart(5, '0')}</strong></p>
                    <p>Refund Amount: <strong>&#8377;${(result.refundAmount || 0).toLocaleString('en-IN')}</strong></p>
                    <p style="margin-top:0.5rem;color:var(--text-light);font-size:0.85rem;">Refund will be processed in 5-7 business days.</p>
                    <p style="color:var(--text-light);font-size:0.85rem;">A cancellation confirmation email has been sent.</p>
                    <button class="btn-book" style="margin-top:1.5rem;" onclick="closeModal('bookingModal');location.reload();">Done</button>
                </div>
            `;
        } else {
            alert(result.error || 'Failed to cancel booking.');
        }
    } catch {
        alert('Failed to cancel booking. Please try again.');
    }
}

// ── Auth Modal ──
function setupAuthModal() {
    document.querySelectorAll('.auth-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            document.querySelectorAll('.auth-panel').forEach(p => p.style.display = 'none');
            document.getElementById(tab.dataset.panel).style.display = 'block';
        });
    });
}

function openAuthModal() {
    document.getElementById('authModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (res.ok) {
            currentUser = await res.json();
            closeModal('authModal');
            updateAuthUI();
        } else {
            alert('Invalid credentials. Please try again.');
        }
    } catch {
        alert('Login failed. Please try again.');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const data = {
        name: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value
    };
    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (res.ok) {
            currentUser = await res.json();
            closeModal('authModal');
            updateAuthUI();
        }
    } catch {
        alert('Registration failed.');
    }
}

function updateAuthUI() {
    const btn = document.getElementById('authBtn');
    if (currentUser) {
        btn.textContent = currentUser.name;
        btn.onclick = null;
    }
}

function scrollToPackages() {
    document.getElementById('packagesSection').scrollIntoView({ behavior: 'smooth' });
}

// ── Fallback sample data ──
function getSamplePackages() {
    return [
        { id:1, name:"Andaman Beach Paradise", destination:"Andaman & Nicobar Islands",
          description:"Explore pristine beaches of Havelock Island with snorkeling and stunning sunsets.",
          price:45999, durationDays:5, hotelName:"Taj Exotica", cabType:"AC Sedan",
          ticketDetails:"Return flights + Ferry", localTourInfo:"Cellular Jail, Snorkeling",
          imageUrl:"images/beach.webp", rating:4.8 },
        { id:2, name:"Heritage Fort Explorer", destination:"Port Blair Heritage Trail",
          description:"Dive into the rich history of Andaman Islands and colonial architecture.",
          price:38499, durationDays:4, hotelName:"Fortune Resort", cabType:"AC SUV",
          ticketDetails:"Return flights + Ferry", localTourInfo:"Cellular Jail, Museum",
          imageUrl:"images/monument.jpeg", rating:4.6 }
    ];
}
