import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import NewsModal from '../components/NewsModal';
import './NewsList.css';

const NewsList = () => {
  const navigate = useNavigate();
  const [newsList, setNewsList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedNews, setSelectedNews] = useState(null);

  // 뉴스 목록 조회
  useEffect(() => {
    const fetchNewsList = async () => {
      try {
        const response = await axios.get('/news');
        setNewsList(response.data);
      } catch (error) {
        console.error('뉴스 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchNewsList();
  }, []);

  // 뉴스 상세 모달 열기
  const handleNewsClick = async (title) => {
    try {
      const response = await axios.get(`/news/detail/${encodeURIComponent(title)}`);
      setSelectedNews(response.data);
      setShowModal(true);
    } catch (error) {
      console.error('뉴스 상세 조회 실패:', error);
    }
  };

  // 메인 페이지로 이동
  const handleGoToMain = () => {
    navigate('/users/main');
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedNews(null);
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="news-list-container">
      <div className="news-list-box">
        <div className="p-2 text-center">
          <h2>오늘의 뉴스</h2>
        </div>
        
        <div className="news-list-content">
          <div>
            <ol className="large-numbers">
              {newsList.map((news, index) => (
                <li key={index}>
                  <button
                    className="news-link"
                    onClick={() => handleNewsClick(news.title)}
                  >
                    {news.title}
                  </button>
                </li>
              ))}
            </ol>
          </div>
        </div>
        
        <div className="button-container">
          <button className="custom-btn btn-3" onClick={handleGoToMain}>
            <span>메인으로 돌아가기</span>
          </button>
        </div>
      </div>

      {/* 뉴스 상세 모달 */}
      {showModal && selectedNews && (
        <NewsModal
          news={selectedNews}
          isOpen={showModal}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

export default NewsList; 