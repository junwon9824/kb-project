import React, { useState } from "react";
import axios from "../apis/axios";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";
import { userApi } from "../apis";

const LoginPage = () => {
  const navigate = useNavigate();
  const [isRightPanelActive, setIsRightPanelActive] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // 로그인 상태
  const [loginData, setLoginData] = useState({
    userid: "",
    password: "",
  });

  // 회원가입 상태
  const [signupData, setSignupData] = useState({
    username: "",
    userid: "",
    password: "",
    phone: "",
    address: "",
    account_password: "",
    disabled: false,
  });

  // 로그인 처리
  const handleLogin = async (e) => {
    e.preventDefault();
    
    if (isLoading) {
      console.log("로그인 진행 중... 중복 요청 방지");
      return;
    }
    
    setIsLoading(true);
    setErrorMessage("");
    
    try {
      console.log("로그인 시도:", loginData);
      const response = await userApi.login(loginData);
      console.log("로그인 응답:", response);
      
      if (response && response.token) {
        console.log("토큰 저장됨:", response.token);
        // 토큰 저장 후 약간의 지연을 두고 메인 페이지로 이동
        setTimeout(() => {
          console.log("메인 페이지로 이동 시도...");
          navigate("/users/main");
        }, 100);
      } else {
        console.error("로그인 응답에 토큰이 없음:", response);
        setErrorMessage("로그인에 실패했습니다.");
      }
    } catch (error) {
      console.error("로그인 에러:", error);
      setErrorMessage("로그인에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  // 회원가입 처리
  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      const response = await userApi.signup(signupData);
      console.log("signup succ");
      setIsRightPanelActive(false);

      // 회원가입 성공 시 동작
    } catch (error) {
      setErrorMessage("회원가입에 실패했습니다.");
    }
  };

  // 입력 필드 변경 처리
  const handleLoginChange = (e) => {
    setLoginData({
      ...loginData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSignupChange = (e) => {
    setSignupData({
      ...signupData,
      [e.target.name]: e.target.value,
    });
  };

  // 장애인 여부 변경 처리
  const handleDisabledChange = (value) => {
    setSignupData({
      ...signupData,
      disabled: value,
    });
  };

  return (
    <div
      className={`container ${isRightPanelActive ? "right-panel-active" : ""}`}
    >
      {errorMessage && (
        <div
          style={{ color: "red", textAlign: "center", marginBottom: "20px" }}
        >
          {errorMessage}
        </div>
      )}

      {/* 로그인 폼 */}
      <div className="container__form container--signin">
        <form className="form" onSubmit={handleLogin}>
          <h2 className="form__title">로그인</h2>
          <input
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            type="text"
            placeholder="아이디"
            className="input"
            name="userid"
            value={loginData.userid}
            onChange={handleLoginChange}
          />
          <input
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            type="password"
            placeholder="비밀번호"
            className="input"
            name="password"
            value={loginData.password}
            onChange={handleLoginChange}
          />
          <button type="submit" className="custom-btn btn-3" disabled={isLoading}>
            <span>{isLoading ? "로그인 중..." : "로그인"}</span>
          </button>
        </form>
      </div>

      {/* 회원가입 폼 */}
      <div className="container__form container--signup">
        <form className="form" onSubmit={handleSignup}>
          <h2 className="form__title">회원가입</h2>
          <input
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            type="text"
            placeholder="이름"
            className="input"
            name="username"
            value={signupData.username}
            onChange={handleSignupChange}
            required
          />
          <input
            type="text"
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            placeholder="아이디"
            className="input"
            name="userid"
            value={signupData.userid}
            onChange={handleSignupChange}
            required
          />
          <input
            type="password"
            placeholder="비밀번호"
            className="input"
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            name="password"
            value={signupData.password}
            onChange={handleSignupChange}
            required
          />
          <input
            type="text"
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            placeholder="전화번호"
            className="input"
            name="phone"
            value={signupData.phone}
            onChange={handleSignupChange}
            required
          />
          <input
            type="text"
            placeholder="주소"
            className="input"
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            name="address"
            value={signupData.address}
            onChange={handleSignupChange}
            required
          />
          <input
            type="text"
            placeholder="계좌비밀번호"
            className="input"
            style={{ outline: "2px solid #e9e9e9", borderRadius: "5px" }}
            name="account_password"
            value={signupData.account_password}
            onChange={handleSignupChange}
            required
          />

          <div style={{ marginTop: "10px" }}>
            <input
              type="radio"
              id="disabled"
              name="disabled"
              checked={signupData.disabled === true}
              onChange={() => handleDisabledChange(true)}
              required
            />
            <label className="custom-control-label" htmlFor="disabled">
              장애
            </label>
            <input
              type="radio"
              id="disabled2"
              name="disabled"
              checked={signupData.disabled === false}
              onChange={() => handleDisabledChange(false)}
              required
            />
            <label className="custom-control-label" htmlFor="disabled2">
              비장애
            </label>
          </div>
          <button className="custom-btn btn-3" type="submit">
            <span>회원가입</span>
          </button>
        </form>
      </div>

      {/* 오버레이 */}
      <div className="container__overlay">
        <div className="overlay">
          <div className="overlay__panel overlay--left">
            <button
              className="custom-btn btn-3"
              onClick={() => setIsRightPanelActive(false)}
            >
              <span>로그인</span>
            </button>
          </div>
          <div className="overlay__panel overlay--right">
            <button
              className="custom-btn btn-3"
              onClick={() => setIsRightPanelActive(true)}
            >
              <span>회원가입</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
