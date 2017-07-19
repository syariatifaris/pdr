package com.seoultechappsoftlab.wireloc.entities;

public class MatrixRSSI {
	private String MatrixMAC;
	private double MatrixRssi;
	public MatrixRSSI(){}
	public MatrixRSSI(String matrixMAC, double matrixRssi) {
		MatrixMAC = matrixMAC;
		MatrixRssi = matrixRssi;
	}
	public String getMatrixMAC() {
		return MatrixMAC;
	}
	public void setMatrixMAC(String matrixMAC) {
		MatrixMAC = matrixMAC;
	}
	public double getMatrixRssi() {
		return MatrixRssi;
	}
	public void setMatrixRssi(double matrixRssi) {
		MatrixRssi = matrixRssi;
	}
}
