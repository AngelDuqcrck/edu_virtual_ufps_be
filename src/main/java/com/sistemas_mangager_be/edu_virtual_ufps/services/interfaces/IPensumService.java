package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumSemestreResponse;

public interface IPensumService {
    
    public PensumDTO crearPensum(PensumDTO pensumDTO) throws ProgramaNotFoundException;

    PensumSemestreResponse listarPensum(Integer id) throws PensumNotFoundException;

    public void vincularMoodleId(MoodleRequest moodleRequest)
            throws PensumNotFoundException;
    PensumDTO actualizarPensum(PensumDTO pensumDTO, Integer id) throws PensumNotFoundException, ProgramaNotFoundException;

    List<PensumSemestreResponse> listarPensums();

    // Cambiado de List<PensumDTO> a List<PensumSemestreResponse>
    List<PensumSemestreResponse> listarPensumsPorPrograma(Integer id) throws ProgramaNotFoundException;

     public void vincularSemestreMoodleId(MoodleRequest moodleRequest)
            throws ProgramaNotFoundException;
}
