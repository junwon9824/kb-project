import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { bankAccountApi } from '../apis/bankAccountApi';
import Header from '../components/Header';
import './TransferPage.css';

const TransferPage = () => {
  const navigate = useNavigate();
  const [userBankAccounts, setUserBankAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
  
  const [transferData, setTransferData] = useState({
    recipient_name: '',
    recipient_banknumber: '',
    sender_banknumber: '',
    amount: '',
    category: '송금',
    account_password: ''
  });

  // 사용자 계좌 목록 조회
  useEffect(() => {
    const fetchUserBankAccounts = async () => {
      try {
        // 현재 로그인한 사용자의 userid를 가져와야 합니다
        // 임시로 하드코딩하거나 localStorage에서 가져오기
        const userid = localStorage.getItem('userid') || 'junwon9824'; // 임시
        console.log("요청할 userid:", userid);
        
        const response = await bankAccountApi.getAccountList(userid);
        console.log("전체 응답:", response);
        console.log("response.data:", response.data);
        console.log("response.data 타입:", typeof response.data);
        console.log("Array.isArray(response.data):", Array.isArray(response.data));
        
        // 응답이 배열인지 확인하고 안전하게 설정
        const accounts = Array.isArray(response.data) ? response.data : [];
        console.log("최종 accounts:", accounts);
        console.log("accounts 길이:", accounts.length);
        
        setUserBankAccounts(accounts);
      } catch (error) {
        console.error('계좌 조회 실패:', error);
        setUserBankAccounts([]); // 오류 시 빈 배열로 설정
      } finally {
        setLoading(false);
      }
    };

    fetchUserBankAccounts();
  }, []);

  // 입력 필드 변경 처리
  const handleChange = (e) => {
    setTransferData({
      ...transferData,
      [e.target.name]: e.target.value
    });
  };

  // 송금 처리
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    try {
      const response = await bankAccountApi.transfer(transferData);
      if (response.status === 200) {
        // 송금 성공 시 메인 페이지로 이동
        navigate('/users/main?successMessage=송금이 완료되었습니다.');
      }
    } catch (error) {
      if (error.response && error.response.data) {
        setErrorMessage(error.response.data);
      } else {
        setErrorMessage('송금 처리 중 오류가 발생했습니다.');
      }
    }
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <>
      <Header />
      <div className="transfer-container">
        <div className="transfer-form-container">
          <form className="transfer-form" onSubmit={handleSubmit}>
            <div className="p-4 text-center">
              <h2>송금</h2>
            </div>
            
            <div className="mb-3">
              <label htmlFor="recipient_name" className="form-label">
                받는사람 이름:
              </label>
              <input
                type="text"
                className="form-control"
                id="recipient_name"
                name="recipient_name"
                value={transferData.recipient_name}
                onChange={handleChange}
              />
            </div>
            
            <div className="mb-3">
              <label htmlFor="recipient_banknumber" className="form-label">
                받는사람 계좌번호:
              </label>
              <input
                type="text"
                className="form-control"
                id="recipient_banknumber"
                name="recipient_banknumber"
                value={transferData.recipient_banknumber}
                onChange={handleChange}
                pattern="\d{4,20}"
                title="숫자만 입력해주세요"
                maxLength="20"
                required
              />
            </div>
            
            <div className="mb-3">
              <label htmlFor="sender_banknumber" className="form-label">
                본인 계좌번호:
              </label>
              <select
                className="form-select"
                id="sender_banknumber"
                name="sender_banknumber"
                value={transferData.sender_banknumber}
                onChange={handleChange}
                required
              >
                <option value="">계좌를 선택하세요</option>
                {userBankAccounts && userBankAccounts.length > 0 ? (
                  userBankAccounts.map((account) => {
                    console.log("계좌 정보:", account);
                    return (
                      <option key={account.id} value={account.accountNumber}>
                        {account.accountNumber} - {account.bankName || 'KB국민은행'}
                      </option>
                    );
                  })
                ) : (
                  <option value="9824">9824 - KB국민은행</option>
                )}
              </select>
            </div>
            
            <div className="mb-3">
              <label htmlFor="amount" className="form-label">
                송금액:
              </label>
              <input
                type="text"
                className="form-control"
                id="amount"
                name="amount"
                value={transferData.amount}
                onChange={handleChange}
                maxLength="20"
              />
            </div>
            
            <div className="mb-3">
              <label htmlFor="account_password" className="form-label">
                계좌 비밀번호:
              </label>
              <input
                type="password"
                className="form-control"
                id="account_password"
                name="account_password"
                value={transferData.account_password}
                onChange={handleChange}
                required
              />
            </div>

            {errorMessage && (
              <div className="error-message">
                <p style={{ color: '#FE3004' }}>{errorMessage}</p>
              </div>
            )}

            <div className="frame">
              <button type="submit" className="custom-btn btn-3">
                <span>송금</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default TransferPage; 