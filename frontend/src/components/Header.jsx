import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Header.css';

const Header = () => {
  const navigate = useNavigate();
  const [isMobileNavOpen, setIsMobileNavOpen] = useState(false);

  const handleLogout = () => {
    // 로그아웃 처리
    navigate('/users/login');
  };

  const toggleMobileNav = () => {
    setIsMobileNavOpen(!isMobileNavOpen);
  };

  return (
    <header 
      id="header" 
      className="header d-flex align-items-center"
      style={{
        background: 'rgb(0, 172, 238)',
        background: 'linear-gradient(0deg, rgba(0, 172, 238, 1) 0%, rgba(2, 126, 251, 1) 100%)'
      }}
    >
      <div className="container-fluid container-xl d-flex align-items-center justify-content-between">
        <Link to="/users/main" className="logo d-flex align-items-center">
          <h1>
            <span style={{ fontSize: '1.5em' }}>KB 4조</span>
          </h1>
        </Link>
        
        <i 
          className={`mobile-nav-toggle ${isMobileNavOpen ? 'mobile-nav-hide' : 'mobile-nav-show'} bi bi-list`}
          onClick={toggleMobileNav}
        ></i>
        
        <nav id="navbar" className={`navbar ${isMobileNavOpen ? 'navbar-mobile' : ''}`}>
          <ul>
            <li>
              <Link to="/users/main">
                <span style={{ fontSize: '1.5em' }}>Main</span>
              </Link>
            </li>
            <li>
              <Link to="/transfer">
                <span style={{ fontSize: '1.5em' }}>송금</span>
              </Link>
            </li>
            <li>
              <Link to="/bankaccounts">
                <span style={{ fontSize: '1.5em' }}>계좌조회</span>
              </Link>
            </li>
            <li>
              <Link to="/bookmarks">
                <span style={{ fontSize: '1.5em' }}>즐겨찾기</span>
              </Link>
            </li>
            <li>
              <Link to="/news">
                <span style={{ fontSize: '1.5em' }}>뉴스</span>
              </Link>
            </li>
            <li>
              <button onClick={handleLogout} className="logout-btn">
                <span style={{ fontSize: '1.5em' }}>로그아웃</span>
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
};

export default Header; 