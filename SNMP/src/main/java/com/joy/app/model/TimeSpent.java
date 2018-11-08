package com.joy.app.model;

import java.util.ArrayList;
import java.util.List;

public class TimeSpent {
	
	private String hash;
	
	private int[] libraryBuilding = new int[4];
	private int[] academicBuilding = new int[6];
	private int[] lectureBlock = new int[3];
	private int[] boysHostel = new int[7];
	private int[] girlsHostel = new int[7];
	private int[] serviceBlock = new int[3];
	private int[] diningBlock = new int[4];
	private int[] facultyResidence = new int[13];
	private int[] OutsideHostel = new int[1];
	private int[] OutsideLibrary = new int[1];
	private int[] Disconnected = new int[1];
	
	
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public int[] getLibraryBuilding() {
		return libraryBuilding;
	}
	public void setLibraryBuilding(int[] libraryBuilding) {
		this.libraryBuilding = libraryBuilding;
	}
	public int[] getAcademicBuilding() {
		return academicBuilding;
	}
	public void setAcademicBuilding(int[] academicBuilding) {
		this.academicBuilding = academicBuilding;
	}
	public int[] getLectureBlock() {
		return lectureBlock;
	}
	public void setLectureBlock(int[] lectureBlock) {
		this.lectureBlock = lectureBlock;
	}
	public int[] getBoysHostel() {
		return boysHostel;
	}
	public void setBoysHostel(int[] boysHostel) {
		this.boysHostel = boysHostel;
	}
	public int[] getGirlsHostel() {
		return girlsHostel;
	}
	public void setGirlsHostel(int[] girlsHostel) {
		this.girlsHostel = girlsHostel;
	}
	public int[] getServiceBlock() {
		return serviceBlock;
	}
	public void setServiceBlock(int[] serviceBlock) {
		this.serviceBlock = serviceBlock;
	}
	public int[] getDiningBlock() {
		return diningBlock;
	}
	public void setDiningBlock(int[] diningBlock) {
		this.diningBlock = diningBlock;
	}
	public int[] getFacultyResidence() {
		return facultyResidence;
	}
	public void setFacultyResidence(int[] facultyResidence) {
		this.facultyResidence = facultyResidence;
	}
	public int[] getOutsideHostel() {
		return OutsideHostel;
	}
	public void setOutsideHostel(int[] outsideHostel) {
		OutsideHostel = outsideHostel;
	}
	public int[] getOutsideLibrary() {
		return OutsideLibrary;
	}
	public void setOutsideLibrary(int[] outsideLibrary) {
		OutsideLibrary = outsideLibrary;
	}
	public int[] getDisconnected() {
		return Disconnected;
	}
	public void setDisconnected(int[] disconnected) {
		Disconnected = disconnected;
	}
	
	
	
}
