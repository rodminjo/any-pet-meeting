package com.backend.accountmanagement.account.infrastructure.entity;

import com.backend.accountmanagement.account.domain.RoleHierarchy;
import com.backend.accountmanagement.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(
    name = "role_hierarchy",
    indexes = {
    @Index(name = "role_hierarchy_idx_id", columnList = "id"),
    @Index(name = "role_hierarchy_idx_child_name", columnList = "child_name"),
    @Index(name = "role_hierarchy_idx_parent_name", columnList = "parent_name")
    })
public class RoleHierarchyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(name = "child_name")
    private String childName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_name", referencedColumnName = "child_name")
    private RoleHierarchyEntity parentName;

    @OneToMany(mappedBy = "parentName", fetch = FetchType.LAZY)
    private Set<RoleHierarchyEntity> roleHierarchy = new HashSet<>();


    public RoleHierarchy toDomain(){
        return RoleHierarchy.builder()
            .id(id)
            .childName(childName)
            .parentName(!ObjectUtils.isEmpty(parentName) ? parentName.toDomain() : null)
            .build();

    }

    public static RoleHierarchyEntity from(RoleHierarchy roleHierarchy){
        if (!ObjectUtils.isEmpty(roleHierarchy)){
            RoleHierarchyEntity roleHierarchyEntity = new RoleHierarchyEntity();
            roleHierarchyEntity.id = roleHierarchy.getId();
            roleHierarchyEntity.childName = roleHierarchy.getChildName();
            roleHierarchyEntity.parentName = RoleHierarchyEntity.from(roleHierarchy.getParentName());
            return roleHierarchyEntity;

        }

        return null;

    }

}
