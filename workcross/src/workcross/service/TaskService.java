package workcross.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.*;

import workcross.model.*;
import workcross.repository.CommentRepository;
import workcross.repository.EntryRepository;
import workcross.repository.ProjectRepository;
import workcross.repository.TaskMemberRepository;
import workcross.repository.TaskRepository;
import workcross.repository.TeamMemberRepository;
import workcross.repository.TeamRepository;
import workcross.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	EntryRepository entryRepository;

	@Autowired
	TaskMemberRepository taskMemberRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@PersistenceContext
	public EntityManager em;

	public Entry addEntry(long projectId, String entryName, double pos) {
		Entry entry = new Entry(projectId, entryName, pos);
		return entryRepository.save(entry);
	}

	public Task addTask(long projectId, long entryId, String taskName,
			String description, double pos) {
		Task task = new Task(projectId, entryId, taskName, description, pos);
		return taskRepository.save(task);
	}

	public void removeTask(Task task) {
		taskRepository.delete(task);
	}

	public void removeEntry(Entry entry) {
		entryRepository.delete(entry);
	}

	public void removeTask(long id) {
		taskRepository.delete(id);
	}

	public void removeEntry(long id) {
		entryRepository.delete(id);
	}

	public List<Task> getProjectTasks(Project project) {
		return taskRepository.findByProjectId(project.getId());
	}

	public List<Entry> getProjectEntries(Project project) {
		return entryRepository.findByProjectId(project.getId());
	}

	public TaskMember addTaskMember(Task task, User user) {
		TaskMember taskMember = new TaskMember(task.getId(), user.getId(),
				"member");
		return taskMemberRepository.save(taskMember);
	}

	public TaskMember addTaskWatcher(Task task, User user) {
		TaskMember taskMember = new TaskMember(task.getId(), user.getId(),
				"watcher");
		return taskMemberRepository.save(taskMember);
	}

	public Task getTaskById(long taskId) {
		return taskRepository.findById(taskId);
	}

	public List<Long> getTaskMemberUserIds(Task task) {
		Query query = em
				.createQuery("select userId from TaskMember where taskId=:taskId");
		query.setParameter("taskId", task.getId());
		List result = query.getResultList();
		return new ArrayList<Long>(result);
	}

	public Task fillTaskMember(Task task) {
		List<Long> userIds = getTaskMemberUserIds(task);
		if (!userIds.isEmpty())
			task.setMembers(userRepository.findByIdIn(userIds));
		else
			task.setMembers(new ArrayList<User>());
		return task;
	}

}
