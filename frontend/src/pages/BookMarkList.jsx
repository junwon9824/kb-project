import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Header from '../components/Header';
import TransferModal from '../components/TransferModal';
import './BookMarkList.css';

const BookMarkList = () => {
  const navigate = useNavigate();
  const [bookMarks, setBookMarks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState(''); // 'transfer' or 'create'
  const [selectedAccountNumber, setSelectedAccountNumber] = useState('');

  // 즐겨찾기 목록 조회
  useEffect(() => {
    const fetchBookMarks = async () => {
      try {
        const response = await axios.get('/bookmarks');
        setBookMarks(response.data);
      } catch (error) {
        console.error('즐겨찾기 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchBookMarks();
  }, []);

  // 즐겨찾기 삭제
  const handleDelete = async (id) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      try {
        await axios.delete(`/bookmarks/${id}`);
        // 목록 다시 조회
        const response = await axios.get('/bookmarks');
        setBookMarks(response.data);
      } catch (error) {
        console.error('즐겨찾기 삭제 실패:', error);
      }
    }
  };

  // 송금 모달 열기
  const handleTransfer = (accountNumber) => {
    setSelectedAccountNumber(accountNumber);
    setModalType('transfer');
    setShowModal(true);
  };

  // 즐겨찾기 추가 모달 열기
  const handleAddBookMark = () => {
    setModalType('create');
    setShowModal(true);
  };

  // 메인 페이지로 이동
  const handleGoToMain = () => {
    navigate('/users/main');
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedAccountNumber('');
    setModalType('');
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <>
      <Header />
      <div className="bookmark-container">
        <div className="bookmark-box">
          <div className="p-2 text-center">
            <h2>즐겨찾기</h2>
          </div>
          
          <div className="bookmark-table-container">
            {bookMarks.length > 0 ? (
              bookMarks.map((bookMark) => (
                <table key={bookMark.id} className="bookmark-table">
                  <thead>
                    <tr>
                      <th>이름</th>
                      <th>계좌번호</th>
                      <th>삭제</th>
                      <th>송금</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>{bookMark.bookMarkName}</td>
                      <td>{bookMark.bookMarkAccountNumber}</td>
                      <td>
                        <button
                          className="delete-btn"
                          onClick={() => handleDelete(bookMark.id)}
                        >
                          삭제
                        </button>
                      </td>
                      <td>
                        <button
                          className="transfer-btn"
                          onClick={() => handleTransfer(bookMark.bookMarkAccountNumber)}
                        >
                          송금
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              ))
            ) : (
              <p>즐겨찾기가 없습니다.</p>
            )}
          </div>
          
          <div className="button-container">
            <button className="custom-btn btn-3" onClick={handleAddBookMark}>
              <span>즐겨찾기 추가</span>
            </button>
            <button className="custom-btn btn-3" onClick={handleGoToMain}>
              <span>메인으로 돌아가기</span>
            </button>
          </div>
        </div>
      </div>

      {/* 모달 */}
      {showModal && (
        <TransferModal
          isOpen={showModal}
          onClose={handleCloseModal}
          type={modalType}
          accountNumber={selectedAccountNumber}
        />
      )}
    </>
  );
};

export default BookMarkList; 