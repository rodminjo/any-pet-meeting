package com.backend.accountmanagement.account.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.backend.accountmanagement.account.domain.RoleHierarchy;
import com.backend.accountmanagement.account.infrastructure.entity.RoleHierarchyEntity;
import com.backend.accountmanagement.account.infrastructure.jpa.RoleHierarchyJpaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class RoleHierarchyRepositoryTest {

  @Mock
  private RoleHierarchyJpaRepository roleHierarchyJpaRepository;

  @InjectMocks
  private RoleHierarchyRepositoryImpl roleHierarchyRepositoryImpl;


  private RoleHierarchyEntity tree1node1;
  private RoleHierarchyEntity tree2node1;
  private RoleHierarchyEntity tree2node2;
  private RoleHierarchyEntity tree3node1;
  private RoleHierarchyEntity tree3node2;
  private RoleHierarchyEntity tree3node3;

  @BeforeEach
  void setUp(){
    tree1node1 = RoleHierarchyEntity.builder()
        .id(1L)
        .childName("tree1node1")
        .parentName(null)
        .roleHierarchy(new HashSet<>())
        .build();

    tree2node1 = RoleHierarchyEntity.builder()
        .id(2L)
        .childName("tree2node1")
        .parentName(tree1node1)
        .roleHierarchy(new HashSet<>())
        .build();

    tree2node2 = RoleHierarchyEntity.builder()
        .id(3L)
        .childName("tree2node2")
        .parentName(tree1node1)
        .roleHierarchy(new HashSet<>())
        .build();

    tree3node1 = RoleHierarchyEntity.builder()
        .id(4L)
        .childName("tree3node1")
        .parentName(tree2node2)
        .roleHierarchy(new HashSet<>())
        .build();

    tree3node2 = RoleHierarchyEntity.builder()
        .id(5L)
        .childName("tree3node2")
        .parentName(tree2node2)
        .roleHierarchy(new HashSet<>())
        .build();

    tree3node3 = RoleHierarchyEntity.builder()
        .id(6L)
        .childName("tree3node3")
        .parentName(tree2node2)
        .roleHierarchy(new HashSet<>())
        .build();

    tree1node1.getRoleHierarchy().addAll(Set.of(tree2node1, tree2node2));
    tree2node2.getRoleHierarchy().addAll(Set.of(tree3node1, tree3node2, tree3node3));
  }

  @Test
  void 모든_RoleHierarchy_를_찾아온다(){
    // given
    when(roleHierarchyJpaRepository.findAll())
        .thenReturn(List.of(tree1node1, tree2node1, tree2node2, tree3node1, tree3node2, tree3node3));

    // when
    List<RoleHierarchy> all = roleHierarchyRepositoryImpl.findAll();
    RoleHierarchy tree1node1Rh = all.stream()
        .filter(rh -> rh.getChildName().equals(tree1node1.getChildName())).findFirst().get();
    RoleHierarchy tree2node2Rh = all.stream()
        .filter(rh -> rh.getChildName().equals(tree2node2.getChildName())).findFirst().get();
    RoleHierarchy tree3node1Rh = all.stream()
        .filter(rh -> rh.getChildName().equals(tree3node1.getChildName())).findFirst().get();

    // then
    assertThat(all.size()).isEqualTo(6);
    assertThat(tree1node1Rh).isNotNull();
    assertThat(tree2node2Rh).isNotNull();
    assertThat(tree3node1Rh).isNotNull();
    assertThat(tree1node1Rh.getParentName()).isNull();
    assertThat(tree2node2Rh.getParentName()).isNotNull();
    assertThat(tree3node1Rh.getParentName()).isNotNull();
    assertThat(tree2node2Rh.getParentName().getChildName()).isEqualTo(tree1node1Rh.getChildName());
    assertThat(tree3node1Rh.getParentName().getChildName()).isEqualTo(tree2node2Rh.getChildName());
  }

}