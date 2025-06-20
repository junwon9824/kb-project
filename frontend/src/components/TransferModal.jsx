import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './TransferModal.css';

const TransferModal = ({ isOpen, onClose, type, accountNumber }) => {
  const [transferData, setTransferData] = useState({
    recipient_name: '',
    recipient_banknumber: accountNumber || '',
    sender_banknumber: '',
    amount: '',
    account_password: ''
  });
  const [userBankAccounts, setUserBankAccounts] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (accountNumber) {
      setTransferData(prev => ({
        ...prev,
        recipient_banknumber: accountNumber
      }));
    }
    
    // 사용자 계좌 목록 조회
    const fetchUserBankAccounts = async () => {
      try {
        const response = await axios.get('/bankaccounts');
        setUserBankAccounts(response.data);
      } catch (error) {
        console.error('계좌 조회 실패:', error);
      }
    };

    if (isOpen) {
      fetchUserBankAccounts();
    }
  }, [isOpen, accountNumber]);

  const handleChange = (e) => {
    setTransferData({
      ...transferData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    try {
      if (type === 'transfer') {
        // 송금 처리
        await axios.post('/bookmarks/transferbookmark', transferData);
        alert('송금이 완료되었습니다.');
      } else if (type === 'create') {
        // 즐겨찾기 추가 처리
        await axios.post('/bookmarks', transferData);
        alert('즐겨찾기가 추가되었습니다.');
      }
      onClose();
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMessage(error.response.data);
      } else {
        setErrorMessage('처리 중 오류가 발생했습니다.');
      }
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <span className="close" onClick={onClose}>&times;</span>
        
        <form onSubmit={handleSubmit}>
          <h2>{type === 'transfer' ? '즐겨찾기 송금' : '즐겨찾기 추가'}</h2>
          
          {type === 'create' && (
            <>
              <div className="form-group">
                <label>받는사람 이름:</label>
                <input
                  type="text"
                  name="recipient_name"
                  value={transferData.recipient_name}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>받는사람 계좌번호:</label>
                <input
                  type="text"
                  name="recipient_banknumber"
                  value={transferData.recipient_banknumber}
                  onChange={handleChange}
                  required
                />
              </div>
            </>
          )}
          
          {type === 'transfer' && (
            <>
              <div className="form-group">
                <label>본인 계좌번호:</label>
                <select
                  name="sender_banknumber"
                  value={transferData.sender_banknumber}
                  onChange={handleChange}
                  required
                >
                  <option value="">계좌를 선택하세요</option>
                  {userBankAccounts.map((account) => (
                    <option key={account.id} value={account.accountNumber}>
                      {account.accountNumber}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>송금액:</label>
                <input
                  type="text"
                  name="amount"
                  value={transferData.amount}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>계좌 비밀번호:</label>
                <input
                  type="password"
                  name="account_password"
                  value={transferData.account_password}
                  onChange={handleChange}
                  required
                />
              </div>
            </>
          )}

          {errorMessage && (
            <div className="error-message">
              <p>{errorMessage}</p>
            </div>
          )}

          <div className="modal-buttons">
            <button type="submit" className="custom-btn btn-3">
              <span>{type === 'transfer' ? '송금' : '추가'}</span>
            </button>
            <button type="button" className="custom-btn btn-3" onClick={onClose}>
              <span>취소</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TransferModal; 