function apiUrl (path) {
  if (window.location.hostname === 'school-lu.kbra.lu') {
    return `https://api.school-lu.kbra.lu/${path}`
  }

  if (
    window.location.hostname === 'localhost' ||
    window.location.hostname === '127.0.0.1'
  ) {
    return `http://localhost:8080/${path}`
  }

  return `https://api.school-lu.kbra.lu/${path}`
}

let csrf

async function initCsrf () {
  const response = await fetch(apiUrl('csrf'), {
    method: 'GET',
    credentials: 'include'
  })

  if (!response.ok) {
    throw new Error('Failed to obtain CSRF token')
  }

  csrf = await response.json()
  console.log('got', csrf)
}

async function getJSON (path) {
  const response = await fetch(apiUrl(path), {
    method: 'GET',
    credentials: 'include',
    headers: {
      Accept: 'application/json'
    }
  })
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`)
  }
  return response.json()
}

async function postJSON (path, body) {
  const response = await fetch(apiUrl(path), {
    method: 'POST',
    credentials: 'include',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
      [csrf.headerName]: csrf.token
    },
    body: JSON.stringify(body)
  })
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`)
  }
  return response.json()
}

async function putJSON (path, body) {
  const response = await fetch(apiUrl(path), {
    method: 'PUT',
    credentials: 'include',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
      [csrf.headerName]: csrf.token
    },
    body: JSON.stringify(body)
  })
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`)
  }
}
