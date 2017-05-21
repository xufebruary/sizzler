package com.sizzler.domain.widget.dto;

import java.io.Serializable;
import java.util.List;

import com.sizzler.domain.variable.dto.DeleteVariables;

public class UpdateWidget extends AcceptWidget implements Serializable {

  private static final long serialVersionUID = 84352392932124665L;

  private List<DeleteVariables> deleteVariablesId;

  private List<DeleteVariables> insertVariablesId;

  public List<DeleteVariables> getDeleteVariablesId() {
    return deleteVariablesId;
  }

  public void setDeleteVariablesId(List<DeleteVariables> deleteVariablesId) {
    this.deleteVariablesId = deleteVariablesId;
  }

  public List<DeleteVariables> getInsertVariablesId() {
    return insertVariablesId;
  }

  public void setInsertVariablesId(List<DeleteVariables> insertVariablesId) {
    this.insertVariablesId = insertVariablesId;
  }
}
