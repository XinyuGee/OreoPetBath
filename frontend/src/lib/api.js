export async function apiFetch(url, options = {}) {
  const token = localStorage.getItem("jwt");
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };
  const res = await fetch(url, { ...options, headers });
  return res;
}