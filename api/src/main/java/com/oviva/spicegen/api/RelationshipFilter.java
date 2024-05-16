package com.oviva.spicegen.api;

import java.util.Optional;

public final class RelationshipFilter {

  private final String resourceKind;

  /** optional */
  private final String resourceId;

  /** optional */
  private final String relation;

  /** optional */
  private final SubjectFilter subjectFilter;

  private RelationshipFilter(Builder builder) {
    resourceKind = builder.resourceKind;
    resourceId = builder.resourceId;
    relation = builder.relation;
    subjectFilter = builder.subjectFilter;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String resourceKind() {
    return resourceKind;
  }

  public Optional<String> resourceId() {
    return Optional.ofNullable(resourceId);
  }

  public Optional<String> relation() {
    return Optional.ofNullable(relation);
  }

  public Optional<SubjectFilter> subjectFilter() {
    return Optional.ofNullable(subjectFilter);
  }

  public static class SubjectFilter {

    private final String subjectKind;

    /** optional */
    private final String subjectId;

    /** optional */
    private final String relation;

    private SubjectFilter(Builder builder) {
      subjectKind = builder.subjectKind;
      subjectId = builder.subjectId;
      relation = builder.relation;
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public String subjectKind() {
      return subjectKind;
    }

    public Optional<String> subjectId() {
      return Optional.ofNullable(subjectId);
    }

    public Optional<String> relation() {
      return Optional.ofNullable(relation);
    }

    public static final class Builder {
      private String subjectKind;
      private String subjectId;
      private String relation;

      private Builder() {}

      public Builder withSubjectKind(String val) {
        subjectKind = val;
        return this;
      }

      public Builder withSubjectId(String val) {
        subjectId = val;
        return this;
      }

      public Builder withRelation(String val) {
        relation = val;
        return this;
      }

      public SubjectFilter build() {
        return new SubjectFilter(this);
      }
    }
  }

  public static final class Builder {
    private String resourceKind;
    private String resourceId;
    private String relation;
    private SubjectFilter subjectFilter;

    private Builder() {}

    public Builder withResourceKind(String val) {
      resourceKind = val;
      return this;
    }

    public Builder withResourceId(String val) {
      resourceId = val;
      return this;
    }

    public Builder withRelation(String val) {
      relation = val;
      return this;
    }

    public Builder withSubjectFilter(SubjectFilter val) {
      subjectFilter = val;
      return this;
    }

    public RelationshipFilter build() {
      return new RelationshipFilter(this);
    }
  }
}
