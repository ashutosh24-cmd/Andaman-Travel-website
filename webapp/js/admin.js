const API = '/api';
let adminPackages = [];
let adminBookings = [];

document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
});

// ── Tab Navigation ──
function showTab(tab) {
    document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.sidebar-link').forEach(l => l.classList.remove('active'));
    document.getElementById('tab-' + tab).classList.add('active');
    event.currentTarget.classList.add('active');

    if (tab === 'dashboard') loadDashboard();
    if (tab === 'bookings') loadBookings();
    if (tab === 'packages') loadAdminPackages();
    if (tab === 'users') loadUsers();
}

// ── Dashboard ──
async function loadDashboard() {
    try {
        const [pkgRes, bookRes] = await Promise.all([
            fetch(`${API}/packages`), fetch(`${API}/bookings`)
        ]);
        adminPackages = await pkgRes.json();
        adminBookings = await bookRes.json();

        document.getElementById('totalBookings').textContent = adminBookings.length;
        document.getElementById('totalPackages').textContent = adminPackages.length;

        const revenue = adminBookings.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
        document.getElementById('totalRevenue').innerHTML = `&#8377;${revenue.toLocaleString('en-IN')}`;

        // Users count from bookings (unique emails)
        const uniqueEmails = new Set(adminBookings.map(b => b.email));
        document.getElementById('totalUsers').textContent = uniqueEmails.size;

        // Recent bookings
        renderRecentBookings(adminBookings.slice(-5).reverse());

        // Package performance
        renderPackagePerformance(adminPackages, adminBookings);
    } catch (err) {
        console.error('Dashboard load error:', err);
    }
}

function renderRecentBookings(bookings) {
    const el = document.getElementById('recentBookingsTable');
    if (bookings.length === 0) {
        el.innerHTML = '<p class="empty-state">No bookings yet. Bookings will appear here when customers book packages.</p>';
        return;
    }
    el.innerHTML = `<table class="admin-table">
        <thead><tr><th>ID</th><th>Customer</th><th>Amount</th><th>Status</th></tr></thead>
        <tbody>${bookings.map(b => `
            <tr>
                <td>#UA${String(b.id).padStart(5, '0')}</td>
                <td>${b.customerName}</td>
                <td>&#8377;${(b.totalAmount || 0).toLocaleString('en-IN')}</td>
                <td><span class="badge badge-${(b.status || 'confirmed').toLowerCase()}">${b.status || 'CONFIRMED'}</span></td>
            </tr>`).join('')}
        </tbody></table>`;
}

function renderPackagePerformance(packages, bookings) {
    const el = document.getElementById('perfBars');
    const colors = ['#0ea5e9', '#10b981', '#f97316', '#8b5cf6', '#ec4899', '#06b6d4'];
    const maxPrice = Math.max(...packages.map(p => p.price));

    el.innerHTML = packages.map((p, i) => {
        const bookCount = bookings.filter(b => b.packageId === p.id).length;
        const width = Math.max((p.price / maxPrice) * 100, 15);
        return `<div class="perf-bar-item">
            <div class="perf-bar-label">
                <span>${p.name}</span>
                <span>${bookCount} bookings | &#8377;${p.price.toLocaleString('en-IN')}</span>
            </div>
            <div class="perf-bar-track">
                <div class="perf-bar-fill" style="width:${width}%;background:${colors[i % colors.length]}"></div>
            </div>
        </div>`;
    }).join('');
}

// ── Bookings Management ──
async function loadBookings() {
    try {
        const res = await fetch(`${API}/bookings`);
        adminBookings = await res.json();
        renderBookingsTable(adminBookings);
    } catch { renderBookingsTable([]); }
}

function renderBookingsTable(bookings) {
    const tbody = document.getElementById('bookingsTableBody');
    if (bookings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="empty-state">No bookings found</td></tr>';
        return;
    }
    tbody.innerHTML = bookings.map(b => {
        const pkg = adminPackages.find(p => p.id === b.packageId);
        return `<tr>
            <td><strong>#UA${String(b.id).padStart(5, '0')}</strong></td>
            <td>${b.customerName}</td>
            <td>${b.email}</td>
            <td>${pkg ? pkg.name : 'Package #' + b.packageId}</td>
            <td>${b.travelDate || '-'}</td>
            <td>${b.numTravelers}</td>
            <td><strong>&#8377;${(b.totalAmount || 0).toLocaleString('en-IN')}</strong></td>
            <td><span class="badge badge-${(b.status || 'confirmed').toLowerCase()}">${b.status || 'CONFIRMED'}</span></td>
            <td>
                <button class="btn-admin-sm btn-view" onclick="alert('Booking #UA${String(b.id).padStart(5, '0')}\\nCustomer: ${b.customerName}\\nEmail: ${b.email}\\nPhone: ${b.phone}\\nTravel Date: ${b.travelDate}\\nTravelers: ${b.numTravelers}\\nTotal: Rs.${b.totalAmount}')">View</button>
                <button class="btn-admin-sm btn-cancel" onclick="cancelBooking(${b.id})">Cancel</button>
            </td>
        </tr>`;
    }).join('');
}

function filterBookings() {
    const q = document.getElementById('bookingSearch').value.toLowerCase();
    const filtered = adminBookings.filter(b =>
        b.customerName.toLowerCase().includes(q) ||
        b.email.toLowerCase().includes(q) ||
        String(b.id).includes(q)
    );
    renderBookingsTable(filtered);
}

function cancelBooking(id) {
    if (!confirm('Are you sure you want to cancel this booking? Customer will receive a cancellation email and refund will be initiated.')) return;
    fetch(`${API}/bookings/${id}/cancel`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => res.json())
    .then(result => {
        if (result.message) {
            alert('Booking cancelled! Refund: Rs.' + (result.refundAmount || 0).toLocaleString('en-IN') + '\nCancellation emails sent to customer and admin.');
            loadBookings();
            loadDashboard();
        } else {
            alert(result.error || 'Failed to cancel booking.');
        }
    })
    .catch(() => alert('Failed to cancel booking.'));
}

// ── Packages Management ──
async function loadAdminPackages() {
    try {
        const res = await fetch(`${API}/packages`);
        adminPackages = await res.json();
        renderPackagesTable(adminPackages);
    } catch { renderPackagesTable([]); }
}

function renderPackagesTable(packages) {
    const tbody = document.getElementById('packagesTableBody');
    tbody.innerHTML = packages.map(p => `
        <tr>
            <td>${p.id}</td>
            <td><img src="${p.imageUrl}" alt="${p.name}"></td>
            <td><strong>${p.name}</strong></td>
            <td>${p.destination}</td>
            <td>&#8377;${p.price.toLocaleString('en-IN')}</td>
            <td>${p.durationDays}D/${p.durationDays - 1}N</td>
            <td><span style="color:#fbbf24">&#9733;</span> ${p.rating}</td>
            <td>
                <button class="btn-admin-sm btn-view" onclick="alert('${p.name}\\n\\nHotel: ${p.hotelName}\\nCab: ${p.cabType}\\nTickets: ${p.ticketDetails}\\nTours: ${p.localTourInfo}')">Details</button>
                <button class="btn-admin-sm btn-edit">Edit</button>
            </td>
        </tr>
    `).join('');
}

function openAddPackageModal() {
    document.getElementById('addPackageModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeAdminModal() {
    document.getElementById('addPackageModal').classList.remove('active');
    document.body.style.overflow = '';
}

function addPackage(e) {
    e.preventDefault();
    const newPkg = {
        id: adminPackages.length + 1,
        name: document.getElementById('pkgName').value,
        destination: document.getElementById('pkgDest').value,
        description: document.getElementById('pkgDesc').value,
        price: parseFloat(document.getElementById('pkgPrice').value),
        durationDays: parseInt(document.getElementById('pkgDays').value),
        hotelName: document.getElementById('pkgHotel').value,
        cabType: document.getElementById('pkgCab').value,
        ticketDetails: document.getElementById('pkgTickets').value,
        localTourInfo: document.getElementById('pkgTour').value,
        imageUrl: 'images/beach.webp',
        rating: 4.5
    };
    adminPackages.push(newPkg);
    renderPackagesTable(adminPackages);
    closeAdminModal();
    alert('Package added successfully!');
}

// ── Users ──
async function loadUsers() {
    // Show users from bookings (unique)
    const seen = new Map();
    adminBookings.forEach(b => {
        if (!seen.has(b.email)) {
            seen.set(b.email, { name: b.customerName, email: b.email });
        }
    });
    const users = Array.from(seen.values());
    const tbody = document.getElementById('usersTableBody');
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty-state">No registered users yet. Users appear here after they make bookings.</td></tr>';
        return;
    }
    tbody.innerHTML = users.map((u, i) => `
        <tr>
            <td>${i + 1}</td>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td><span class="badge badge-active">Active</span></td>
        </tr>
    `).join('');
}