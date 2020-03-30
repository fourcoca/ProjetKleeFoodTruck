package com.foodTruckProjet.foodTruck.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.foodTruckProjet.foodTruck.model.Catalogue;
import com.foodTruckProjet.foodTruck.model.Commande;
import com.foodTruckProjet.foodTruck.model.Ligne;
import com.foodTruckProjet.foodTruck.model.Panier;
import com.foodTruckProjet.foodTruck.model.Produit;
import com.foodTruckProjet.foodTruck.model.Type;
import com.foodTruckProjet.foodTruck.model.Utilisateur;
import com.foodTruckProjet.foodTruck.repo.CommandeRepository;
import com.foodTruckProjet.foodTruck.repo.LigneRepository;
import com.foodTruckProjet.foodTruck.repo.ProduitRepository;
import com.foodTruckProjet.foodTruck.repo.TypeRepository;
import com.foodTruckProjet.foodTruck.repo.UtilisateurRepository;

@RestController
public class ControllerFoodTruck {

	@Autowired
	private ProduitRepository prepo;
	@Autowired
	private UtilisateurRepository urepo;
	@Autowired
	private TypeRepository trepo;

	@Autowired
	private CommandeRepository crep;

	@Autowired
	private LigneRepository lrep;
	
	@GetMapping("/accueil")
	public ModelAndView accueil(Model model) {
		ModelAndView modelAndView = new ModelAndView("accueil", "top3", prepo.findtop3());
		return modelAndView;
	}

	@GetMapping(path = "/", produces = "application/json")
	public List<Produit> getEmployees() {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		return prepo.findAll();
	}

	@GetMapping("/catalogue")
	public ModelAndView catalogue(Model model) {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);

		return modelAndView;
	}

	@GetMapping("/recherche")
	public ModelAndView rechercher(Model model,HttpServletRequest ht, @RequestParam("mot") String mot) {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);
		modelAndView.addObject("motRecherche",mot);
//		ht.getSession().setAttribute("type", type);
//		modelAndView.addObject("famille",famille);
		ht.getSession().setAttribute("date", null);
		ht.getSession().setAttribute("livraison", null);
		ht.getSession().setAttribute("heure", null);
		

		return modelAndView;
	}
	@GetMapping("/TypeMode")
	public ModelAndView Type(Model model,HttpServletRequest ht, @RequestParam("ModeType") int ModeType) {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);
		ht.getSession().setAttribute("mode", ModeType);
		ht.getSession().setAttribute("date", null);
		ht.getSession().setAttribute("livraison", null);
		ht.getSession().setAttribute("heure", null);
		return modelAndView;
	}

	@GetMapping("/ajouterQ-{ligne.produit.nom}")
	public ModelAndView ajouterUneQ(Model model, HttpServletRequest ht, @PathVariable(name = "ligne.produit.nom") String produit) {
		ModelAndView modelAndView = new ModelAndView("panier");
		Panier p = (Panier) ht.getSession().getAttribute("Panier");
		p.ajouterQuantite(produit, 1);
		ht.getSession().setAttribute("Panier", p);
		return modelAndView;
	}


	@GetMapping("/suppr-{ligne.produit.nom}")
	public ModelAndView supprimerP(Model model, HttpServletRequest ht, @PathVariable(name = "ligne.produit.nom") String produit) {
		ModelAndView modelAndView = new ModelAndView("panier");
		Panier p = (Panier) ht.getSession().getAttribute("Panier");
		p.supprimerLigne(produit);
		ht.getSession().setAttribute("Panier", p);
		return modelAndView;
	}
	@GetMapping("/diminuerQ-{ligne.produit.nom}")
	public ModelAndView diminuerUneQ(Model model, HttpServletRequest ht, @PathVariable(name = "ligne.produit.nom") String produit) {
		ModelAndView modelAndView = new ModelAndView("panier");
		Panier p = (Panier) ht.getSession().getAttribute("Panier");
		p.diminuerQuantite(produit, 1);
		ht.getSession().setAttribute("Panier", p);
		return modelAndView;
	}	
	@GetMapping("/catalogue-{detailId}")
	public ModelAndView catalogueDetail(Model model, @PathVariable(name = "detailId") int detailId) {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);
		modelAndView.addObject("detail", prepo.findById(detailId).get());
		return modelAndView;
	}

	@PostMapping("/date-livraison")
	public ModelAndView AppliquerDate(Model model, HttpServletRequest ht, @RequestParam("date") String date,
			@RequestParam("livraison") String livraison,
			@RequestParam("heure") String heure, @RequestParam("Type") String type, @RequestParam("Famille") String famille) {
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);
		ht.getSession().setAttribute("date", date);
		ht.getSession().setAttribute("livraison", livraison);
		ht.getSession().setAttribute("heure", heure);
		ht.getSession().setAttribute("famille", famille);
		ht.getSession().setAttribute("type", type);
		System.out.println(heure);
		return modelAndView;
	}

	@PostMapping("/ajouter-panier-{detailId}")
	public ModelAndView AjouterAuPanier(Model model, HttpServletRequest ht, @PathVariable(name = "detailId") int detailId,
			@RequestParam("quantite") int quantite) {
		Produit detail = prepo.findById(detailId).get();
		Catalogue catalogue = new Catalogue(prepo, trepo);
		ModelAndView modelAndView = new ModelAndView("catalogue", "catalogue", catalogue);
		Utilisateur current = (Utilisateur) ht.getSession().getAttribute("utilisateur");
		Panier p;
		if (ht.getSession().getAttribute("Panier") == null) {
			p = new Panier();
		}
		else
		{
			p = (Panier) ht.getSession().getAttribute("Panier");
		}
	
			String adresse = "";
			switch (ht.getSession().getAttribute("livraison").toString()) {
			case "Domicile":
				adresse = current.getAdresse();
				break;
			case "Societe":
				adresse = current.getSociete();
				break;
			case "Sur Place":
				adresse = "Sur place";
				break;
			default:
				break;
			}
			String str = ht.getSession().getAttribute("date")+" "+ht.getSession().getAttribute("heure");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
			Ligne l = new Ligne(quantite, dateTime, adresse, detail);
			p.ajouterLigne(l);
			ht.getSession().setAttribute("Panier", p);
			
		return modelAndView;
	}

	@GetMapping("/connexion")
	public ModelAndView connectionGet(Model model, HttpServletRequest ht) {
		ModelAndView modelAndView = new ModelAndView("connexion", "email", "");
		modelAndView.addObject("pwd", "");
		return modelAndView;
	}

	@PostMapping("/connexion")
	public ModelAndView connectionPost(@RequestParam(name = "email") String email,
			@RequestParam(name = "pwd") String mdp, Model model, HttpServletRequest ht) {
		ModelAndView reussite = new ModelAndView("accueil");
		ModelAndView echec = new ModelAndView("/connexion");
		Utilisateur a = urepo.findByEmailAndMotDePasse(email, mdp);

		if (a != null) {
			ht.getSession().setAttribute("utilisateur", a);
			ht.getSession().setAttribute("erreur", 0);
			return reussite;
		} else {
			ht.getSession().setAttribute("erreur", 1);
			return echec;
		}
	}

	@RequestMapping("/contacts")
	public ModelAndView contacts(Model model) {
		ModelAndView modelAndView = new ModelAndView("contacts");
		return modelAndView;
	}

	@RequestMapping("/inscription")
	public ModelAndView inscription(Model model) {
		ModelAndView modelAndView = new ModelAndView("inscription");
		return modelAndView;
	}

	@RequestMapping("/panier")
	public ModelAndView panier(Model model) {
		ModelAndView modelAndView = new ModelAndView("panier");
		return modelAndView;
	}

	@RequestMapping("/valider")
	public ModelAndView valider(Model model,HttpServletRequest ht) {
		ModelAndView modelAndView = new ModelAndView("valider");
		ModelAndView modelAndViewC = new ModelAndView("accueil");
		Panier p = (Panier) ht.getSession().getAttribute("Panier");
		if(p == null ||  ht.getSession().getAttribute("utilisateur")==null)
		{
			return modelAndViewC;
		}
		Utilisateur u = (Utilisateur) ht.getSession().getAttribute("utilisateur");
		
		for (Ligne ligne : p.getLignes()) {
			int Idproduit = ligne.getProduit().getId();
			Produit produit = prepo.findById(Idproduit).get();
			if(produit.getStock()-ligne.getQuantite()<0)
			{
				produit.setStock(0);
			}
			else
			{
				produit.setStock(produit.getStock()-ligne.getQuantite());
			}
			prepo.save(produit);
			lrep.save(ligne);
		}
		Commande commande = new Commande(LocalDateTime.now(),u,p);
		if(p.getLignes().size()>0)
		{
			crep.save(commande);
		}
		modelAndView.addObject("commande", commande);
		ht.getSession().setAttribute("Panier", new Panier());
		ht.getSession().setAttribute("date", null);
		ht.getSession().setAttribute("livraison", null);
		ht.getSession().setAttribute("heure", null);
		return modelAndView;
	}

	@RequestMapping("/profil")
	public ModelAndView profil(Model model) {
		ModelAndView modelAndView = new ModelAndView("profil/resume");
		return modelAndView;
	}

	@RequestMapping("profil/resume")
	public ModelAndView profilResume(Model model) {
		ModelAndView modelAndView = new ModelAndView("profil/resume");
		return modelAndView;
	}

	@RequestMapping("profil/historique")
	public ModelAndView profilHistorique(Model model) {
		ModelAndView modelAndView = new ModelAndView("profil/historique");
		return modelAndView;
	}

	@RequestMapping("profil/modifier")
	public ModelAndView profilModifier(Model model) {
		ModelAndView modelAndView = new ModelAndView("profil/modifier");
		return modelAndView;
	}

	@RequestMapping("admin")
	public ModelAndView admin(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/authadmin");
		return modelAndView;
	}

	@RequestMapping("admin/listUtilisateur")
	public ModelAndView adminAfficherUtilisateur(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/listUtilisateur");
		return modelAndView;
	}

	@RequestMapping("admin/modifierUtilisateur")
	public ModelAndView adminModifierUtilisateur(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/modifierUtilisateur");
		return modelAndView;
	}

	@RequestMapping("admin/ajouterUtilisateur")
	public ModelAndView adminAjouterUtilisateur(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/ajouterUtilisateur");
		return modelAndView;
	}

	@RequestMapping("admin/listCatalogue")
	public ModelAndView adminAfficherCatalogue(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/listCatalogue");
		return modelAndView;
	}

	@RequestMapping("admin/modifierCatalogue")
	public ModelAndView adminModifierCatalogue(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/modifierCatalogue");
		return modelAndView;
	}

	@RequestMapping("admin/ajouterCatalogue")
	public ModelAndView adminAjouterCatalogue(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/ajouterCatalogue");
		return modelAndView;
	}

	@GetMapping("admin/modifierHoraire")
	public ModelAndView adminModifierHoraire(Model model) {
		ModelAndView modelAndView = new ModelAndView("admin/modifierHoraire");
		modelAndView.addObject("heurePD",trepo.findByNom("Petit_Dejeuner"));
		modelAndView.addObject("heureD",trepo.findByNom("Dejeuner"));
		modelAndView.addObject("heureG",trepo.findByNom("Gouter"));
		modelAndView.addObject("heureDiner",trepo.findByNom("Diner"));
		return modelAndView;
	}
	
	@PostMapping("admin/modifierHoraire")
	public ModelAndView adminModifierHorairePost(Model model,
			@RequestParam("heurePD") String heurePD,
			@RequestParam("heureD") String heureD,
			@RequestParam("heureG") String heureG,
			@RequestParam("heureDiner") String heureDiner) {
		ModelAndView modelAndView = new ModelAndView("admin/modifierHoraire");
		Type PD = trepo.findByNom("Petit_Dejeuner");
		Type D = trepo.findByNom("Dejeuner");
		Type G = trepo.findByNom("Gouter");
		Type Din = trepo.findByNom("Diner");
		
		PD.setHeure(Integer.parseInt(heurePD));
		D.setHeure(Integer.parseInt(heureD));
		G.setHeure(Integer.parseInt(heureG));
		Din.setHeure(Integer.parseInt(heureDiner));
		
		trepo.save(PD);
		trepo.save(D);
		trepo.save(G);
		trepo.save(Din);
		
		return modelAndView;
	}
}
