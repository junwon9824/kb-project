import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import MainPage from './pages/MainPage';
import TransferPage from './pages/TransferPage';
import BankAccountList from './pages/BankAccountList';
import BookMarkList from './pages/BookMarkList';
import VoiceRecognition from './pages/VoiceRecognition';
import NewsList from './pages/NewsList';
import LogList from './pages/LogList';
import LogList2 from './pages/LogList2';
import './App.css';

// 404 페이지 컴포넌트
const NotFound = () => (
  <div style={{ textAlign: 'center', padding: '50px' }}>
    <h1>404 - 페이지를 찾을 수 없습니다</h1>
    <p>요청하신 페이지가 존재하지 않습니다.</p>
    <a href="/users/main">메인 페이지로 돌아가기</a>
  </div>
);

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/" element={<Navigate to="/users/login" replace />} />
                    <Route path="/users/login" element={<LoginPage />} />
                    <Route path="/users/main" element={<MainPage />} />
                    <Route path="/transfer" element={<TransferPage />} />
                    <Route path="/bankaccounts" element={<BankAccountList />} />
                    <Route path="/bookmarks" element={<BookMarkList />} />
                    <Route path="/voiceRecognition" element={<VoiceRecognition />} />
                    <Route path="/news" element={<NewsList />} />
                    <Route path="/log/:accountNumber" element={<LogList />} />
                    <Route path="/log2/:accountNumber" element={<LogList2 />} />
                    <Route path="*" element={<NotFound />} />   
                </Routes>
            </div>
        </Router>
    );
}

export default App;