package com.hizam.task_management_service.repository;

import com.hizam.task_management_service.model.Task;
import com.hizam.task_management_service.model.TaskPage;
import com.hizam.task_management_service.model.TaskSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TaskCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public TaskCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }


    public Page<Task>findAllWithFilters(TaskPage taskPage, TaskSearchCriteria taskSearchCriteria)
    {
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);
        Predicate predicate = getPredicate(taskSearchCriteria,taskRoot);
        criteriaQuery.where(predicate);
        setOrder(taskPage,criteriaQuery,taskRoot);
        TypedQuery<Task> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(taskPage.getPageNumber() * taskPage.getPageSize());
        typedQuery.setMaxResults(taskPage.getPageSize());
        Pageable pageable =  getPageable(taskPage);
        long taskCount = getTaskCount(predicate);
        return new PageImpl<>(typedQuery.getResultList(),pageable,taskCount);
    }



    private Predicate getPredicate(TaskSearchCriteria taskSearchCriteria, Root<Task> taskRoot) {
        List<Predicate>predicates = new ArrayList<>();
        if(Objects.nonNull(taskSearchCriteria.getTitle()))
        {
            predicates.add(criteriaBuilder.like(taskRoot.get("title"),"%"+taskSearchCriteria.getTitle()+"%"));
        }
       return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


    private void setOrder(TaskPage taskPage,
                          CriteriaQuery<Task> criteriaQuery,
                          Root<Task> taskRoot) {
        if(taskPage.getSortDirection().equals(Sort.Direction.ASC))
        {
            criteriaQuery.orderBy(criteriaBuilder.asc(taskRoot.get(taskPage.getSortBy())));
        }else {
            criteriaQuery.orderBy(criteriaBuilder.desc(taskRoot.get(taskPage.getSortBy())));
        }
    }

    private Pageable getPageable(TaskPage taskPage) {
        return PageRequest.of(taskPage.getPageNumber(),taskPage.getPageSize(),Sort.by(taskPage.getSortDirection(),taskPage.getSortBy()));
    }

    private long getTaskCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Task> taskRoot = countQuery.from(Task.class);
        countQuery.select(criteriaBuilder.count(taskRoot)).where(predicate);
        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<Task> query = cb.createQuery(Task.class);
        return entityManager.createQuery(query.createCountQuery()).getSingleResult();
    }

    @Transactional
    public void updateTaskByPerformerId(Task task, Long performerId)
    {
        CriteriaUpdate<Task> cu = criteriaBuilder.createCriteriaUpdate(Task.class);
        Root<Task> taskRoot = cu.from(Task.class);
        cu.set(taskRoot.get("author"),task.getAuthor());
        cu.set(taskRoot.get("comment"),task.getComment());
        cu.set(taskRoot.get("description"),task.getDescription());
        cu.set(taskRoot.get("title"),task.getTitle());
        cu.set(taskRoot.get("taskPriority"),task.getTaskPriority());
        cu.set(taskRoot.get("taskStatus"),task.getTaskStatus());
        cu.where(criteriaBuilder.equal(taskRoot.get("performerId"),performerId));
        entityManager.createQuery(cu).executeUpdate();
    }



}
