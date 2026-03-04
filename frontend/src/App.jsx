import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/common/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import MainPage from './pages/MainPage'
import UserPage from './pages/UserPage'
import BillStdPage from './pages/BillStdPage'
import SubscriptionPage from './pages/SubscriptionPage'
import SubscriptionMainPage from './pages/SubscriptionMainPage'
import StudyLogPage from './pages/StudyLogPage'
import CommonCodePage from './pages/CommonCodePage'
import QnaPage from './pages/QnaPage'
import QnaDetailPage from './pages/QnaDetailPage'
import SpecialSubscriptionPage from './pages/SpecialSubscriptionPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<ProtectedRoute><Navigate to="/main" replace /></ProtectedRoute>} />
        <Route path="/main"          element={<ProtectedRoute><MainPage /></ProtectedRoute>} />
        <Route path="/users"         element={<ProtectedRoute><UserPage /></ProtectedRoute>} />
        <Route path="/subscriptions" element={<ProtectedRoute><SubscriptionPage /></ProtectedRoute>} />
        <Route path="/bill-std"           element={<ProtectedRoute><BillStdPage /></ProtectedRoute>} />
        <Route path="/subscription-main" element={<ProtectedRoute><SubscriptionMainPage /></ProtectedRoute>} />
        <Route path="/study-logs"        element={<ProtectedRoute><StudyLogPage /></ProtectedRoute>} />
        <Route path="/code"    element={<ProtectedRoute><CommonCodePage /></ProtectedRoute>} />
        <Route path="/qna"     element={<ProtectedRoute><QnaPage /></ProtectedRoute>} />
        <Route path="/qna/new" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
        <Route path="/qna/:id" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
        <Route path="/special-subscription" element={<ProtectedRoute><SpecialSubscriptionPage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
