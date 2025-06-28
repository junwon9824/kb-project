import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { bankAccountApi } from "../apis/bankAccountApi"; // 경로에 맞게 수정
import { userApi } from "../apis/userApi";
import Header from "../components/Header";
import BankAccountCreateModal from "../components/BankAccountCreateModal";
import "./BankAccountList.css";

const BankAccountList = () => {
  const navigate = useNavigate();
  const [bankAccounts, setBankAccounts] = useState([]);
  const [userid, setUserid] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);

  // 계좌 목록 조회
  useEffect(() => {
    const fetchBankAccounts = async () => {
      try {
        console.log("before fetch bank accounts ");

        // 사용자 프로필 정보 가져오기
        let userProfile;
        try {
          userProfile = await userApi.getProfile();
          console.log("=== 사용자 프로필 정보 ===");
          console.log("전체 userProfile:", userProfile);
          console.log("userProfile.userid:", userProfile.userid);
          console.log("userProfile type:", typeof userProfile);
          console.log("userProfile keys:", Object.keys(userProfile));
          console.log("=== 사용자 프로필 정보 끝 ===");
        } catch (profileError) {
          console.error("사용자 프로필 조회 실패:", profileError);
          setError("사용자 정보를 가져올 수 없습니다.");
          setLoading(false);
          return;
        }

        if (!userProfile || !userProfile.userid) {
          console.error("사용자 ID가 없습니다.");
          setError("사용자 ID를 찾을 수 없습니다.");
          setLoading(false);
          return;
        }

        setUserid(userProfile.userid);

        // 계좌 목록 가져오기
        const accounts = await bankAccountApi.getAccountList(userProfile.userid);
        console.log("계좌 목록:", accounts);
        setBankAccounts(accounts);
      } catch (error) {
        console.error("계좌 조회 실패:", error);
        setError("계좌 목록을 가져올 수 없습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchBankAccounts();
  }, [navigate]);

  // 페이지 진입 시 히스토리 추가 (뒤로가기 지원)
  useEffect(() => {
    // 현재 페이지를 히스토리에 추가
    window.history.pushState(null, '', window.location.pathname);
  }, []);

  // 계좌 내역 조회
  const handleViewLogs = (accountNumber) => {
    navigate(`/log/${accountNumber}`);
  };

  // 계좌 생성 페이지로 이동
  const handleCreateAccount = () => {
    navigate("/bankaccounts/create");
  };

  // 메인 페이지로 이동
  const handleGoToMain = () => {
    navigate("/users/main");
  };

  // 뒤로가기
  const handleGoBack = () => {
    navigate(-1); // 브라우저 히스토리에서 한 단계 뒤로
  };

  // 금액 포맷팅
  const formatAmount = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount) + "원";
  };

  const handleCreateSuccess = () => {
    // 계좌 생성 성공 후 목록 새로고침
    fetchBankAccounts();
  };

  const handleDeleteAccount = async (accountId) => {
    if (window.confirm('정말로 이 계좌를 삭제하시겠습니까?')) {
      try {
        await bankAccountApi.deleteBankAccount(accountId);
        // 삭제 성공 후 목록 새로고침
        fetchBankAccounts();
      } catch (error) {
        console.error('계좌 삭제 실패:', error);
        alert('계좌 삭제에 실패했습니다.');
      }
    }
  };

  const handleSetMainAccount = async (accountId) => {
    try {
      await bankAccountApi.setMainAccount(accountId);
      // 주계좌 설정 성공 후 목록 새로고침
      fetchBankAccounts();
    } catch (error) {
      console.error('주계좌 설정 실패:', error);
      alert('주계좌 설정에 실패했습니다.');
    }
  };

  if (loading) {
    return <div className="loading">로딩 중...</div>;
  }

  if (error) {
    return <div className="error">에러: {error}</div>;
  }

  console.log("bankAccounts:", bankAccounts, Array.isArray(bankAccounts));

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
            <button className="custom-btn btn-3" onClick={() => setShowCreateModal(true)}>
              <span>계좌생성</span>
            </button>
            {/* <button className="custom-btn btn-3" onClick={handleGoBack}>
              <span>뒤로가기</span>
            </button> */}
            <button className="custom-btn btn-3" onClick={handleGoToMain}>
              <span>메인으로 돌아가기</span>
            </button>
          </div>
        </div>
      </div>

      <BankAccountCreateModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={handleCreateSuccess}
        userid={userid}
      />
    </>
  );
};

export default BankAccountList;
