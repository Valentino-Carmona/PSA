package com.psa.proyecto_api.specification;

import com.psa.proyecto_api.dto.request.ProjectFilterRequest;
import com.psa.proyecto_api.model.Project;
import com.psa.proyecto_api.model.ProjectTag;
import com.psa.proyecto_api.model.enums.ProjectStatus;
import com.psa.proyecto_api.model.enums.ProjectType;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;

@Component
public class ProjectSpecifications {

    /**
     * Creates a specification with all the provided filters combined using AND
     * @param filters The filter request containing status and type filters
     * @return A specification combining all non-null filters
     */
    public Specification<Project> withFilters(ProjectFilterRequest filters) {
        return Specification.allOf(
            hasStatus(filters.getStatus()),
            hasType(filters.getType()),
            hasTag(filters.getTag())
        );
    }

    /**
     * Creates a specification to filter projects by status
     * @param status The status to filter by
     * @return A specification that filters by the given status, or returns all if status is null
     */
    public Specification<Project> hasStatus(ProjectStatus status) {
        return (root, query, criteriaBuilder) -> {
            return status == null ? null : criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Creates a specification to filter projects by type
     * @param type The type to filter by
     * @return A specification that filters by the given type, or returns all if type is null
     */
    public Specification<Project> hasType(ProjectType type) {
        return (root, query, criteriaBuilder) -> {
            return type == null ? null : criteriaBuilder.equal(root.get("type"), type);
        };
    }

    /**
     * Creates a specification to filter projects by tag
     * @param tag The tag to filter by
     * @return A specification that filters by the given tag, or returns all if tag is null
     */
    public Specification<Project> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> {
            if (tag == null) return null;
            
            Join<Project, ProjectTag> tagJoin = root.join("projectTags");
            return criteriaBuilder.equal(tagJoin.get("tagName"), tag.trim());
        };
    }
}
