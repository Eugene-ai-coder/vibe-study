import useStudyLogs from '../hooks/useStudyLogs'
import MainLayout from '../components/common/MainLayout'
import Loading from '../components/common/Loading'
import ErrorMessage from '../components/common/ErrorMessage'
import StudyLogForm from '../components/StudyLogForm'
import StudyLogTable from '../components/StudyLogTable'
import EditModal from '../components/EditModal'

export default function StudyLogPage() {
  const {
    logs, isLoading, error,
    editingLog, setEditingLog,
    fetchLogs, handleCreate, handleDelete, handleUpdate,
  } = useStudyLogs()

  return (
    <MainLayout>
      <StudyLogForm onSubmit={handleCreate} />

      {isLoading ? (
        <Loading />
      ) : error ? (
        <ErrorMessage message={error} onRetry={fetchLogs} />
      ) : (
        <StudyLogTable
          logs={logs}
          onDelete={handleDelete}
          onEditClick={setEditingLog}
        />
      )}

      <EditModal
        log={editingLog}
        onSubmit={handleUpdate}
        onClose={() => setEditingLog(null)}
      />
    </MainLayout>
  )
}
