import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { bankAccountApi } from '../apis/bankAccountApi'; // 경로에 맞게 수정
import Header from '../components/Header';
import './BankAccountList.css';

const BankAccountList = () => {
  const navigate = useNavigate();
  const [bankAccounts, setBankAccounts] = useState([]);
  const [loading, setLoading] = useState(true);

  // 계좌 목록 조회
  useEffect(() => {
    const fetchBankAccounts = async () => {
      try {
        const response = await bankAccountApi.getAccountList();
        // response가 배열인지, response.data가 배열인지 확인해서 아래 둘 중 하나 선택
        setBankAccounts(Array.isArray(response) ? response : response.data);
      } catch (error) {
        console.error('계좌 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchBankAccounts();
  }, []);

  // 계좌 내역 조회
  const handleViewLogs = (accountNumber) => {
    navigate(`/log/${accountNumber}`);
  };

  // 계좌 생성 페이지로 이동
  const handleCreateAccount = () => {
    navigate('/bankaccounts/create');
  };

  // 메인 페이지로 이동
  const handleGoToMain = () => {
    navigate('/users/main');
  };

  // 금액 포맷팅
  const formatAmount = (amount) => {
    return new Intl.NumberFormat('ko-KR').format(amount) + '원';
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }
  console.log('bankAccounts:', bankAccounts, Array.isArray(bankAccounts));

  return (
    <>
      <Header />
      <div className="bank-account-container">
        <div className="bank-account-box">
          <div className="p-2 text-center">
            <h2>계좌조회</h2>
          </div>
          
          {bankAccounts && bankAccounts.length > 0 ? (
            <div className="container account-list-container">
              <div className="account-info-container">
                {bankAccounts.map((bankAccount) => (
                  <div key={bankAccount.id} className="account-info">
                    <div className="left-section">
                      <div className="bank-name">
                        {bankAccount.bank?.bankname}
                      </div>
                    </div>
                    <div className="left-section">
                      <div className="account-number">
                        {bankAccount.accountNumber}
                      </div>
                    </div>
                    <hr />
                    <span className="balance">
                      {formatAmount(bankAccount.amount)}
                    </span>
                    <button
                      className="btn"
                      onClick={() => handleViewLogs(bankAccount.accountNumber)}
                    >
                      계좌내역
                    </button>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="no-accounts">
              <p>No bank accounts found.</p>
            </div>
          )}
          
          <div className="button-container">
            <button className="custom-btn btn-3" onClick={handleCreateAccount}>
              <span>계좌생성</span>
            </button>
            <button className="custom-btn btn-3" onClick={handleGoToMain}>
              <span>메인으로 돌아가기</span>
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default BankAccountList;

