package com.utc2.facilityui.model;

import com.utc2.facilityui.controller.FacilityController;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class OperationsTableCellFactory<S> implements Callback<TableColumn<S, Void>, TableCell<S, Void>> {

    private final OperationsTableCell.OperationsEventHandler<S> eventHandler;

    public OperationsTableCellFactory(FacilityController handler) {
        this.eventHandler = (OperationsTableCell.OperationsEventHandler<S>) handler;
    }

    @Override
    public TableCell<S, Void> call(TableColumn<S, Void> param) {
        OperationsTableCell<S> cell = new OperationsTableCell<>();
        cell.setEventHandler(eventHandler);
        return cell;
    }
}
