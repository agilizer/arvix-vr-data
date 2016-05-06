package cn.arvix.vrdata.repository;

import cn.arvix.vrdata.domain.SyncTaskContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wanghaiyang on 16/5/5.
 */
public interface SyncTaskContentRepository extends JpaRepository<SyncTaskContent, Long> {

    @Query("select s from SyncTaskContent s join s.author u where u.id=?1 and s.taskType=?2")
    public List<SyncTaskContent> getTaskByUserId(Long userId, SyncTaskContent.TaskType taskType);

    @Query("select s from SyncTaskContent s join s.author u where u.id=?1")
    public List<SyncTaskContent> getTaskByUserId(Long userId);

    @Query("select s from SyncTaskContent s where s.taskStatus=1")
    public List<SyncTaskContent> getWaitingTask();

    @Query("select s from SyncTaskContent s where s.caseId=?1")
    public SyncTaskContent findOneByCaseId(String caseId);

    @Query("select s from SyncTaskContent s where s.caseId=?1 and s.taskType=?2")
    public SyncTaskContent findOneByCaseIdAndTaskType(String caseId, SyncTaskContent.TaskType taskType);

    @Transactional
    @Modifying
    @Query("delete from SyncTaskContent s where s.caseId=?1")
    public void deleteByCaseId(String caseId);

    @Transactional
    @Modifying
    @Query("delete from SyncTaskContent s where s.caseId=?1 and s.taskType=?2")
    public void deleteTask(String caseId, SyncTaskContent.TaskType taskType);
}
