package com.EBI.springproject.service;

import com.EBI.springproject.Exception.CustomException;
import com.EBI.springproject.Exception.GlobalException;
import com.EBI.springproject.model.EmployeeDto;
import com.EBI.springproject.Entity.EmployeeEntity;
import com.EBI.springproject.model.EmployeeSaveDto;
import com.EBI.springproject.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    final EmployeeRepo employeeRepo;
    final ModelMapper modelMapper;

    public List<EmployeeDto> getAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepo.findAll();

        if (employeeEntities.isEmpty()) {
            throw new CustomException("400","not found exception","employee table isEmpty!!");
        }
        List<EmployeeDto> employeeDtos = new ArrayList<>();

        employeeDtos = employeeEntities.stream().map(employeeEntity -> modelMapper.map(employeeEntity, EmployeeDto.class)).collect(Collectors.toList());

        return employeeDtos;
    }

    public EmployeeDto getEmployeeById(Long id) {
        Optional<EmployeeEntity> employeeEntity = employeeRepo.findById(id);
        if (employeeEntity.isEmpty()) {
            throw new CustomException("400","not found exception","no employee found");
        }
        return employeeEntity.map(entity -> modelMapper.map(entity, EmployeeDto.class)).orElse(null);
    }

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {

        if (employeeDto.getSecond_Name().isEmpty() || employeeDto.getFirst_Name().isEmpty() || employeeDto.getSalary().isEmpty() ) {
            throw new CustomException("400","not found exception","missing data to register!");
        }


        EmployeeEntity employeeEntity = employeeRepo.save(modelMapper.map(employeeDto, EmployeeEntity.class));
        return modelMapper.map(employeeEntity, EmployeeDto.class);
    }


    public EmployeeSaveDto patchEmployee(EmployeeSaveDto employeeSaveDto) {
        EmployeeEntity savedEmployeeEntity = null;


        if (employeeSaveDto.getId() != null) {
            Optional<EmployeeEntity> employeeEntityOptional = employeeRepo.findById( employeeSaveDto.getId());

           if (employeeEntityOptional.isPresent()) {
                if(employeeSaveDto.getSalary() != null && !employeeSaveDto.getSalary().isEmpty())
                {
                    employeeEntityOptional.get().setSalary(employeeSaveDto.getSalary());
                }
                if (employeeSaveDto.getFirst_Name() != null && !employeeSaveDto.getFirst_Name().isEmpty())
                {
                    employeeEntityOptional.get().setFirst_Name(employeeSaveDto.getFirst_Name());
                }
                if (employeeSaveDto.getSecond_Name() != null && !employeeSaveDto.getSecond_Name().isEmpty())
                {
                    employeeEntityOptional.get().setSecond_Name(employeeSaveDto.getSecond_Name());
                }


                savedEmployeeEntity = employeeRepo.save(employeeEntityOptional.get());
            }
           else  throw new CustomException("400","not found exception","employee is not present!");


        }
        else  throw new CustomException("400","not found exception","ID cannot be null");


        return modelMapper.map(savedEmployeeEntity, EmployeeSaveDto.class);
    }

    public EmployeeSaveDto UpdateEmployee(EmployeeSaveDto employeeSaveDto) {
        if (employeeSaveDto.getId() == null) {
            throw new CustomException("400","not found exception","id cannot be null");
        }

        EmployeeEntity employeeEntity = modelMapper.map(employeeSaveDto, EmployeeEntity.class);
        EmployeeEntity employeeEntity1 = employeeRepo.save(employeeEntity);
        return modelMapper.map(employeeEntity1, EmployeeSaveDto.class);


    }

    public void deleteEmployee(Long id) {

        EmployeeEntity employeeEntity = employeeRepo.findById(id).orElse(null);
        if(employeeEntity == null)
            throw new CustomException("400","not found exception","no employee to delete");
        employeeRepo.delete(employeeEntity);
    }
}
