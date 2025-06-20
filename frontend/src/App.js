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
        </Routes>
      </div>
    </Router>
  );
}

export default App; 