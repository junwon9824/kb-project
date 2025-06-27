//axios.interceptors.response.use(
//  response => response,
//  error => {
//    if (error.response && (error.response.status === 403 || error.response.status === 401)) {
//      localStorage.removeItem('accessToken');
//      window.location.href = '/login';
//    }
//    return Promise.reject(error);
//  }
//);
//
