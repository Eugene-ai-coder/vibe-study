import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/common/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import MainPage from './pages/MainPage'
import UserPage from './pages/UserPage'
import BillStdPage from './pages/BillStdPage'
import SubscriptionPage from './pages/SubscriptionPage'
import StudyLogPage from './pages/StudyLogPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<ProtectedRoute><Navigate to="/main" replace /></ProtectedRoute>} />
        <Route path="/main"          element={<ProtectedRoute><MainPage /></ProtectedRoute>} />
        <Route path="/users"         element={<ProtectedRoute><UserPage /></ProtectedRoute>} />
        <Route path="/subscriptions" element={<ProtectedRoute><SubscriptionPage /></ProtectedRoute>} />
        <Route path="/bill-std"      element={<ProtectedRoute><BillStdPage /></ProtectedRoute>} />
        <Route path="/study-logs"    element={<ProtectedRoute><StudyLogPage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
