package com.seoultechappsoftlab.wireloc.models;

import java.util.List;

import android.content.Context;

import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.services.StageService;

public class StageModel {
	
	private int currentStage;
	private Stage stage;
	private List<Stage> stageList;
	private StageService stageService;

	/**
	 * Constructor
	 */
	public StageModel(Context context) {
		this.stageService = new StageService(context);
	}

	/**
	 * Get Current Stage
	 * @return
	 */
	public int getCurrentStage() {
		return currentStage;
	}

	/**
	 * Set Current Stage
	 * @param currentStage
	 */
	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
	}

	/**
	 * Get Stage
	 * @return
	 */
	public Stage getStage() {
		this.stage = this.stageService.getById(currentStage);
		return stage;
	}

	/**
	 * Set Stage
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Get Stage List
	 * @return
	 */
	public List<Stage> getStageList() {
		this.stageList = this.stageService.getAll();
		return stageList;
	}

}
