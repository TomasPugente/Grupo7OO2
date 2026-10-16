package com.example.unla.grupo7.services.implementation;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.dtos.FestivalDTO;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.services.IFestivalService;

@Service ("festivalService")
public class FestivalService implements IFestivalService{
    private final IFestivalRepository festivalRepository;

    public FestivalService(IFestivalRepository festivalRepository){

        this.festivalRepository= festivalRepository;
    }

    public List<Festival> getAll(){
        return festivalRepository.findAll();
    }

    @Override
    public void insertOrUpdate(FestivalDTO festivalDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertOrUpdate'");
    }

    @Override
    public boolean remove(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }
}
