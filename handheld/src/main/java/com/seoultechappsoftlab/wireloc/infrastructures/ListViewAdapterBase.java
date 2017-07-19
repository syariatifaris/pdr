package com.seoultechappsoftlab.wireloc.infrastructures;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class ListViewAdapterBase<E extends EntityBase> extends BaseAdapter {	
	
	protected LayoutInflater layoutInflater;
	protected List<E> dataCollection;
	
	public ListViewAdapterBase(Context context){
		this.dataCollection = new ArrayList<E>();
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public abstract void setData(List<E> dataCollection);
	public abstract List<E> getDataCollection();
	public abstract E getData(int position);
}
