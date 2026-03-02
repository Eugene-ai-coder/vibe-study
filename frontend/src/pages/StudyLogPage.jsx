import useStudyLogs from '../hooks/useStudyLogs'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import Loading from '../components/common/Loading'
import StudyLogForm from '../components/studylog/StudyLogForm'
import StudyLogTable from '../components/studylog/StudyLogTable'
import EditModal from '../components/studylog/EditModal'

export default function StudyLogPage() {
  const {
    logs, isLoading, errorMsg, successMsg,
    setErrorMsg, setSuccessMsg,
    editingLog, setEditingLog,
    handleCreate, handleDelete, handleUpdate,
  } = useStudyLogs()

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg} type="error" onClose={() => setErrorMsg(null)} />

      <div className="space-y-4 pb-20">
        <h1 className="text-xl font-bold text-gray-800">학습 로그</h1>

        <StudyLogForm onSubmit={handleCreate} />

        {isLoading ? (
          <Loading />
        ) : (
          <StudyLogTable
            logs={logs}
            onDelete={handleDelete}
            onEditClick={setEditingLog}
          />
        )}
      </div>

      <EditModal
        log={editingLog}
        onSubmit={handleUpdate}
        onClose={() => setEditingLog(null)}
      />
    </MainLayout>
  )
}
