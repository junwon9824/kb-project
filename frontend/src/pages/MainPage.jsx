import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Header from '../components/Header';
import './MainPage.css';

const MainPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    // 토큰 확인
    const token = localStorage.getItem("authToken");
    if (!token) {
      console.log("메인 페이지: 토큰이 없음 - 로그인 페이지로 이동");
      navigate("/users/login");
      return;
    }

    // URL 파라미터에서 성공 메시지 확인
    const params = new URLSearchParams(location.search);
    const message = params.get('successMessage');
    if (message) {
      setSuccessMessage(message);
      alert(message);
    }
  }, [location, navigate]);

  const handleNavigation = (path) => {
    navigate(path);
  };

  return (
    <>
      <Header />
      <div className="container main-container">
        <div className="row justify-content-center col-md-12 mx-auto">
          <div className="col-4 mb-3">
            <div id="command">
              <button
                style={{ width: '400px', height: '250px' }}
                className="custom-btn btn-3"
                onClick={() => handleNavigation('/transfer')}
              >
                <span id="btn-lg-font">송금</span>
              </button>
            </div>
          </div>
          <div className="col-4 mb-3">
            <button
              style={{ width: '400px', height: '250px' }}
              className="custom-btn btn-3"
              onClick={() => handleNavigation('/bookmarks')}
            >
              <span id="btn-lg-font">즐겨찾기</span>
            </button>
          </div>
          <div className="col-4">
            <button
              style={{ width: '400px', height: '250px' }}
              className="custom-btn btn-3"
              onClick={() => handleNavigation('/bankaccounts')}
            >
              <span id="btn-lg-font">계좌조회</span>
            </button>
          </div>

          <div className="col-4">
            <button
              style={{ width: '400px', height: '250px' }}
              className="custom-btn btn-3"
              onClick={() => handleNavigation('/voiceRecognition')}
            >
              <span id="btn-lg-font">음성 인식</span>
            </button>
          </div>

          <div className="col-4">
            <button
              style={{ width: '400px', height: '250px' }}
              className="custom-btn btn-3"
              onClick={() => handleNavigation('/news')}
            >
              <span id="btn-lg-font">뉴스</span>
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default MainPage; 