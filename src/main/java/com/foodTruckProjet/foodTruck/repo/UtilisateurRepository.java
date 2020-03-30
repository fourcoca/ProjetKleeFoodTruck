package com.foodTruckProjet.foodTruck.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodTruckProjet.foodTruck.model.Utilisateur;
 	
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer>{

	Utilisateur findByEmail(String email);
	Utilisateur findByEmailAndMotDePasse(String email,String mdp);
	
}
