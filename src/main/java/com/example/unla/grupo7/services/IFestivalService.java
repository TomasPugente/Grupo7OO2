package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.FestivalDTO;
import com.example.unla.grupo7.entities.Festival;

public interface IFestivalService {
    List<Festival> getAll();

	void insertOrUpdate(FestivalDTO festivalDTO);

	boolean remove(long id);
}
