// src/main/resources/static/js/app.js

const AUTH_ME         = '/api/auth/me';
const USERS_REGISTER  = '/api/users/register';
const API_BASE        = '/api';

let authHeader  = null;
let currentUser = null;

// ——— Initialization ———
window.onload = async () => {
  // Показваме само формата за вход/регистрация при старт
  document.getElementById('main-container').style.display = 'none';
  document.getElementById('auth-container').style.display = 'block';

  // Превключване Login / Register табове
  document.getElementById('tab-login').onclick    = () => toggleTab('login');
  document.getElementById('tab-register').onclick = () => toggleTab('register');

  // Бутони за вход, регистрация и изход
  document.getElementById('login-button').onclick    = login;
  document.getElementById('register-button').onclick = register;
  document.getElementById('logout-button').onclick   = () => {
    authHeader  = null;
    currentUser = null;
    showAuth();
  };
};


function toggleTab(view) {
  document.getElementById('login-view').style.display    = view === 'login'    ? 'block' : 'none';
  document.getElementById('register-view').style.display = view === 'register' ? 'block' : 'none';
  document.getElementById('auth-error').innerText = '';
}


function showAuth() {
  document.getElementById('auth-container').style.display = 'block';
  document.getElementById('main-container').style.display = 'none';
}
function showApp() {
  document.getElementById('auth-container').style.display = 'none';
  document.getElementById('main-container').style.display = 'block';
  document.getElementById('user-info').innerText =
    `${currentUser.username} (${currentUser.userType})`;
  buildMenu();
}

// ——— Display auth errors ———
function showAuthError(msg) {
  document.getElementById('auth-error').innerText = msg;
}

// ——— AJAX Registration ———
async function register() {
  const u    = document.getElementById('reg-username').value.trim();
  const p    = document.getElementById('reg-password').value;
  const role = document.getElementById('reg-role').value;
  const link = document.getElementById('reg-link-id').value.trim();

  if (!u || !p) {
    return showAuthError('Потребителско име и парола са задължителни.');
  }

  const params = new URLSearchParams({ username: u, password: p, userType: role });
  if (role === 'CLIENT' && link)    params.append('clientId',   link);
  if (role === 'EMPLOYEE' && link)  params.append('employeeId', link);

  const res = await fetch(`${USERS_REGISTER}?${params}`, { method: 'POST' });
  if (!res.ok) {
    const txt = await res.text();
    return showAuthError(`Грешка при регистрация: ${txt || res.status}`);
  }

  alert('Регистрация успешна! Моля, влезте.');
  toggleTab('login');
}

// ——— AJAX Login via HTTP Basic ———
async function login() {
  const u = document.getElementById('login-username').value.trim();
  const p = document.getElementById('login-password').value;
  if (!u || !p) {
    return showAuthError('Попълнете полетата за вход.');
  }

  authHeader = 'Basic ' + btoa(`${u}:${p}`);

  try {
    const res = await fetch(AUTH_ME, {
      headers: { 'Authorization': authHeader }
    });
    if (!res.ok) throw new Error('Грешни данни за вход');

    currentUser = await res.json();
    showApp();
  } catch (e) {
    authHeader = null;
    showAuthError(e.message);
  }
}

// ——— Helper for protected endpoints ———
async function authFetch(url, opts = {}) {
  opts.headers = {
    ...(opts.headers || {}),
    'Authorization': authHeader,
    'Content-Type': 'application/json'
  };
  const res = await fetch(url, opts);
  if (!res.ok) throw res;
  return res.json();
}

// ——— Build the menu based on role ———
function buildMenu() {
  const nav = document.getElementById('menu');
  nav.innerHTML = '';

  if (currentUser.userType === 'CLIENT') {
      // Clients see only their own shipments
      addNav('Моите пратки', fetchMyShipments);
    } else {
      // Employees see everything
      addNav('Компания',        fetchCompanies);
      addNav('Клиенти',         fetchClients);
      addNav('Служители',       fetchEmployees);
      addNav('Офиси',           fetchOffices);
      addNav('Всички пратки',   fetchShipments);
      addNav('Регистрирай пратка', showRegisterShipmentForm);
      addNav('Справки',           fetchReports);
    }
}
function addNav(label, fn) {
  const btn = document.createElement('button');
  btn.textContent = label;
  btn.onclick     = fn;
  document.getElementById('menu').append(btn);
}


// === COMPANIES CRUD ===
async function fetchCompanies() {
  const data = await authFetch(`${API_BASE}/companies`);
  let html = `<h2>Компания</h2><button onclick="showCompanyForm()">Добави компания</button>`;
  html += `<table><tr><th>ID</th><th>Име</th><th>Адрес</th><th>Телефон</th><th>Действия</th></tr>`;
  data.forEach(c => {
    html += `<tr>
      <td>${c.id}</td><td>${c.name}</td><td>${c.address}</td><td>${c.phone || ''}</td>
      <td>
        <button onclick="editCompany(${c.id})">✏️</button>
        <button onclick="deleteCompany(${c.id})">🗑️</button>
      </td>
    </tr>`;
  });
  html += `</table>`;
  document.getElementById('content').innerHTML = html;
}
function showCompanyForm(c) {
  const isEdit = !!c;
  document.getElementById('content').innerHTML = `
    <h2>${isEdit ? 'Редактирай' : 'Добави'} компания</h2>
    <form onsubmit="submitCompany(event, ${c ? c.id : null})">
      <input id="comp-name"    placeholder="Име"   value="${c ? c.name : ''}" required>
      <input id="comp-address" placeholder="Адрес" value="${c ? c.address : ''}" required>
      <input id="comp-phone"   placeholder="Телефон" value="${c ? c.phone : ''}">
      <button type="submit">Запази</button>
      <button type="button" onclick="fetchCompanies()">Откажи</button>
    </form>`;
}
async function submitCompany(e, id) {
  e.preventDefault();
  const obj = {
    name:    document.getElementById('comp-name').value,
    address: document.getElementById('comp-address').value,
    phone:   document.getElementById('comp-phone').value
  };
  if (id) {
    await authFetch(`${API_BASE}/companies/${id}`, { method: 'PUT',  body: JSON.stringify(obj) });
  } else {
    await authFetch(`${API_BASE}/companies`,       { method: 'POST', body: JSON.stringify(obj) });
  }
  fetchCompanies();
}
async function deleteCompany(id) {
  if (!confirm('Сигурни ли сте?')) return;
  await authFetch(`${API_BASE}/companies/${id}`, { method: 'DELETE' });
  fetchCompanies();
}
async function editCompany(id) {
  const c = await authFetch(`${API_BASE}/companies/${id}`);
  showCompanyForm(c);
}


// === CLIENTS CRUD ===
async function fetchClients() {
  const data = await authFetch(`${API_BASE}/clients`);
  let html = `<h2>Клиенти</h2><button onclick="showClientForm()">Добави клиент</button>`;
  html += `<table><tr><th>ID</th><th>Име</th><th>Email</th><th>Телефон</th><th>Действия</th></tr>`;
  data.forEach(c => {
    html += `<tr>
      <td>${c.id}</td><td>${c.name}</td><td>${c.email}</td><td>${c.phoneNumber}</td>
      <td>
        <button onclick="editClient(${c.id})">✏️</button>
        <button onclick="deleteClient(${c.id})">🗑️</button>
      </td>
    </tr>`;
  });
  html += `</table>`;
  document.getElementById('content').innerHTML = html;
}
function showClientForm(c) {
  const isEdit = !!c;
  document.getElementById('content').innerHTML = `
    <h2>${isEdit ? 'Редактирай' : 'Добави'} клиент</h2>
    <form onsubmit="submitClient(event, ${c ? c.id : null})">
      <input id="client-name"  placeholder="Име"  value="${c ? c.name : ''}" required>
      <input id="client-email" type="email" placeholder="Email" value="${c ? c.email : ''}" required>
      <input id="client-phone" placeholder="Телефон" value="${c ? c.phoneNumber : ''}" required>
      <button type="submit">Запиши</button>
      <button type="button" onclick="fetchClients()">Откажи</button>
    </form>`;
}
async function submitClient(e, id) {
  e.preventDefault();
  const obj = {
    name:        document.getElementById('client-name').value,
    email:       document.getElementById('client-email').value,
    phoneNumber: document.getElementById('client-phone').value
  };
  if (id) {
    await authFetch(`${API_BASE}/clients/${id}`, { method: 'PUT',  body: JSON.stringify(obj) });
  } else {
    await authFetch(`${API_BASE}/clients`,       { method: 'POST', body: JSON.stringify(obj) });
  }
  fetchClients();
}
async function deleteClient(id) {
  if (!confirm('Сигурни ли сте?')) return;
  await authFetch(`${API_BASE}/clients/${id}`, { method: 'DELETE' });
  fetchClients();
}
async function editClient(id) {
  const c = await authFetch(`${API_BASE}/clients/${id}`);
  showClientForm(c);
}


// === EMPLOYEES CRUD ===
async function fetchEmployees() {
  const data = await authFetch(`${API_BASE}/employees`);
  let html = `<h2>Служители</h2><button onclick="showEmployeeForm()">Добави служител</button>`;
  html += `<table><tr><th>ID</th><th>Име</th><th>Офис</th><th>Роля</th><th>Действия</th></tr>`;
  data.forEach(e => {
    html += `<tr>
      <td>${e.id}</td><td>${e.name}</td>
      <td>${e.office.city}, ${e.office.address}</td><td>${e.role}</td>
      <td>
        <button onclick="editEmployee(${e.id})">✏️</button>
        <button onclick="deleteEmployee(${e.id})">🗑️</button>
      </td>
    </tr>`;
  });
  html += `</table>`;
  document.getElementById('content').innerHTML = html;
}
function showEmployeeForm(emp) {
  const isEdit = !!emp;
  document.getElementById('content').innerHTML = `
    <h2>${isEdit ? 'Редактирай' : 'Добави'} служител</h2>
    <form onsubmit="submitEmployee(event, ${emp ? emp.id : null})">
      <input id="emp-name"        placeholder="Име"  value="${emp ? emp.name : ''}" required>
      <input id="emp-office-id"   type="number" placeholder="Office ID" value="${emp ? emp.office.id : ''}" required>
      <select id="emp-role">
        <option value="COURIER"      ${emp && emp.role === 'COURIER' ? 'selected' : ''}>COURIER</option>
        <option value="OFFICE_STAFF" ${emp && emp.role === 'OFFICE_STAFF' ? 'selected' : ''}>OFFICE_STAFF</option>
      </select>
      <button type="submit">Запиши</button>
      <button type="button" onclick="fetchEmployees()">Откажи</button>
    </form>`;
}
async function submitEmployee(e, id) {
  e.preventDefault();
  const obj = {
    name:   document.getElementById('emp-name').value,
    office: { id: +document.getElementById('emp-office-id').value },
    role:   document.getElementById('emp-role').value
  };
  if (id) {
    await authFetch(`${API_BASE}/employees/${id}`, { method: 'PUT',  body: JSON.stringify(obj) });
  } else {
    await authFetch(`${API_BASE}/employees`,       { method: 'POST', body: JSON.stringify(obj) });
  }
  fetchEmployees();
}
async function deleteEmployee(id) {
  if (!confirm('Сигурни ли сте?')) return;
  await authFetch(`${API_BASE}/employees/${id}`, { method: 'DELETE' });
  fetchEmployees();
}
async function editEmployee(id) {
  const e = await authFetch(`${API_BASE}/employees/${id}`);
  showEmployeeForm(e);
}


// === OFFICES CRUD ===
async function fetchOffices() {
  const data = await authFetch(`${API_BASE}/offices`);
  let html = `<h2>Офиси</h2><button onclick="showOfficeForm()">Добави офис</button>`;
  html += `<table><tr><th>ID</th><th>Град</th><th>Адрес</th><th>Действия</th></tr>`;
  data.forEach(o => {
    html += `<tr>
      <td>${o.id}</td><td>${o.city}</td><td>${o.address}</td>
      <td>
        <button onclick="editOffice(${o.id})">✏️</button>
        <button onclick="deleteOffice(${o.id})">🗑️</button>
      </td>
    </tr>`;
  });
  html += `</table>`;
  document.getElementById('content').innerHTML = html;
}
function showOfficeForm(o) {
  const isEdit = !!o;
  document.getElementById('content').innerHTML = `
    <h2>${isEdit ? 'Редактирай' : 'Добави'} офис</h2>
    <form onsubmit="submitOffice(event, ${o ? o.id : null})">
      <input id="off-city"    placeholder="Град"   value="${o ? o.city : ''}" required>
      <input id="off-address" placeholder="Адрес" value="${o ? o.address : ''}" required>
      <button type="submit">Запиши</button>
      <button type="button" onclick="fetchOffices()">Откажи</button>
    </form>`;
}
async function submitOffice(e, id) {
  e.preventDefault();
  const obj = {
    city:    document.getElementById('off-city').value,
    address: document.getElementById('off-address').value
  };
  if (id) {
    await authFetch(`${API_BASE}/offices/${id}`, { method: 'PUT',  body: JSON.stringify(obj) });
  } else {
    await authFetch(`${API_BASE}/offices`,       { method: 'POST', body: JSON.stringify(obj) });
  }
  fetchOffices();
}
async function deleteOffice(id) {
  if (!confirm('Сигурни ли сте?')) return;
  await authFetch(`${API_BASE}/offices/${id}`, { method: 'DELETE' });
  fetchOffices();
}
async function editOffice(id) {
  const o = await authFetch(`${API_BASE}/offices/${id}`);
  showOfficeForm(o);
}


// === SHIPMENTS ===
async function fetchShipments() {
  const data = await authFetch(`${API_BASE}/shipments/all`);
  renderShipmentsTable(data);
}
async function fetchMyShipments() {
  const url = currentUser.userType === 'CLIENT'
    ? `${API_BASE}/shipments/client/${currentUser.client.id}/sent`
    : `${API_BASE}/shipments/all`;
  const data = await authFetch(url);
  renderShipmentsTable(data);
}
function renderShipmentsTable(list) {
  let html = `<h2>Пратки</h2><table>
    <tr><th>ID</th><th>Подател</th><th>Получател</th><th>Адрес</th>
        <th>Тегло</th><th>Метод</th><th>Статус</th><th>Дата рег.</th>
        <th>Регистрирал</th><th>Действие</th></tr>`;
  list.forEach(s => {
    html += `<tr>
      <td>${s.id}</td>
      <td>${s.sender.name}</td>
      <td>${s.receiver.name}</td>
      <td>${s.deliveryAddress}</td>
      <td>${s.weight}</td>
      <td>${s.toOffice ? 'До офис' : 'До адрес'}</td>
      <td>${s.status}</td>
      <td>${s.registrationDate}</td>
      <td>${s.registeredBy ? s.registeredBy.name : ''}</td>
      <td>${
        currentUser.userType === 'EMPLOYEE'
          ? `<button onclick="deliver(${s.id})">Достави</button>` : ''
      }</td>
    </tr>`;
  });
  html += `</table>`;
  document.getElementById('content').innerHTML = html;
}
async function deliver(id) {
  await authFetch(`${API_BASE}/shipments/${id}/deliver`, { method: 'PUT' });
  fetchShipments();
}
function showRegisterShipmentForm() {
  document.getElementById('content').innerHTML = `
    <h2>Регистрирай пратка</h2>
    <form onsubmit="submitShipment(event)">
      <input id="s-sender"   placeholder="ID на подател" required>
      <input id="s-receiver" placeholder="ID на получател" required>
      <input id="s-address"  placeholder="Адрес за доставка" required>
      <input id="s-weight"   type="number" step="0.1" placeholder="Тегло (кг)" required>
      <select id="s-toOffice">
        <option value="false">До адрес</option>
        <option value="true">До офис</option>
      </select>
      <button type="submit">Регистрация</button>
      <button type="button" onclick="fetchShipments()">Откажи</button>
    </form>`;
}
async function submitShipment(e) {
  e.preventDefault();
  const obj = {
    sender:          { id: +document.getElementById('s-sender').value },
    receiver:        { id: +document.getElementById('s-receiver').value },
    deliveryAddress: document.getElementById('s-address').value,
    weight:          +document.getElementById('s-weight').value,
    toOffice:        document.getElementById('s-toOffice').value === 'true'
  };
  await authFetch(`${API_BASE}/shipments/register`, { method: 'POST', body: JSON.stringify(obj) });
  fetchShipments();
}


// === REPORTS ===
function fetchReports() {
  document.getElementById('content').innerHTML = `
    <h2>Справки</h2>
    <button onclick="fetchNotDelivered()">Недоставени пратки</button>
    <button onclick="fetchByEmployeePrompt()">Регистрирани пратки от служител</button>
    <button onclick="fetchByClientSentPrompt()">Изпратени от клиент</button>
    <button onclick="fetchByClientReceivedPrompt()">Получени от клиент</button>
    <button onclick="fetchRevenuePrompt()">Приходи</button>`;
}
async function fetchNotDelivered() {
  const list = await authFetch(`${API_BASE}/shipments/not-delivered`);
  renderShipmentsTable(list);
}
async function fetchByEmployeePrompt() {
  const id = prompt('ID на служител:'); if (!id) return;
  const list = await authFetch(`${API_BASE}/shipments/employee/${id}`);
  renderShipmentsTable(list);
}
async function fetchByClientPrompt() {
  const id = prompt('ID на клиент:'); if (!id) return;
  const list = await authFetch(`${API_BASE}/shipments/client/${id}/sent`);
  renderShipmentsTable(list);
}
async function fetchRevenuePrompt() {
  const start = prompt('Начална дата (YYYY-MM-DD):');
  if (!start) return;
  const end = prompt('Крайна дата (YYYY-MM-DD):');
  if (!end) return;

  try {
    const res = await authFetch(
      `${API_BASE}/shipments/revenue?` +
      `startDate=${encodeURIComponent(start)}` +
      `&endDate=${encodeURIComponent(end)}`
    );
    // authFetch now returns parsed JSON (the number)
    document.getElementById('content').innerHTML =
      `<h2>Приходи от ${start} до ${end}: ${res.toFixed(2)} лева</h2>`;
  } catch (e) {
    alert('Грешка при изчисляване на приходите: ' + e);
  }
}

/**
 * Извиква справка: всички пратки, изпратени от даден клиент
 */
async function fetchByClientSentPrompt() {
  const id = prompt('ID на клиент за изпратени пратки:');
  if (!id) return;
  const list = await authFetch(`${API_BASE}/shipments/client/${id}/sent`);
  renderShipmentsTable(list);
}

/**
 * Извиква справка: всички пратки, получени от даден клиент
 */
async function fetchByClientReceivedPrompt() {
  const id = prompt('ID на клиент за получени пратки:');
  if (!id) return;
  const list = await authFetch(`${API_BASE}/shipments/client/${id}/received`);
  renderShipmentsTable(list);
}