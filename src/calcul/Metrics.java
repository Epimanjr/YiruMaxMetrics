package calcul;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author Maxime BLAISE
 */
public class Metrics {

    /**
     * Le chemin du dossier
     */
    private String cheminDossier;

    /**
     * Le nom du dossier
     */
    private String nomDossier;

    /**
     * Le nombre de lignes totales.
     */
    private int nombreLignes;

    /**
     * Le nombre de lignes vides.
     */
    private int nombreLignesVides;

    /**
     * Le nombre de lignes commentaires.
     */
    private int nombreLignesCommentaires;
    
    /**
     * Nombre de dossiers parcourus.
     */
    private int nombreDossierParcouru;

    /**
     * Constructeur vide.
     */
    public Metrics() {
        // Initialisation
        this.cheminDossier = "";
        this.nomDossier = "";
        this.nombreLignes = 0;
        this.nombreLignesCommentaires=0;
        this.nombreLignesVides=0;
        this.nombreDossierParcouru=0;
    }

    /**
     * Méthode à appeler en premier
     *
     * @param jTable1 table pour afficher le résultat
     * @param label le label d'affichage
     */
    public void calculerNombreLignes(JTable jTable1, JLabel label) {
        // Affichage de JLabel
        label.setText("Statistiques de " + nomDossier);

        // Affichage d'un dialog
        JDialog patience = new JDialog();
        JPanel pan = new JPanel();
        pan.add(new JLabel("Patientez..."));
        patience.setContentPane(pan);
        patience.setLocation(350, 350);
        patience.pack();
        patience.setVisible(true);

        // Réinitialisation du nombre de lignes
        this.nombreLignes = 0;
        this.nombreLignesVides = 0;
        this.nombreLignesCommentaires = 0;
        this.nombreDossierParcouru = 0;
        System.out.print(nombreDossierParcouru+"<-|");

        // Appel de la méthode
        calculerNombreLignes(cheminDossier);
        
        System.out.println("\nNombre total de dossiers parcourus : " + nombreDossierParcouru);

        // Affichage
        jTable1.setValueAt(this.nombreLignes, 0, 1);
        jTable1.setValueAt(this.nombreLignesVides, 1, 1);
        jTable1.setValueAt(this.nombreLignes - this.nombreLignesVides, 2, 1);
        jTable1.setValueAt(this.nombreLignesCommentaires, 3, 1);
        jTable1.setValueAt(this.nombreLignes - this.nombreLignesVides - this.nombreLignesCommentaires, 4, 1);

        // Fin
        patience.dispose();
    }

    /**
     * Méthode qui calcule le nombre de lignes de manière récursive.
     *
     * @param chemin Le chemin du dossier de base
     */
    public void calculerNombreLignes(String chemin) {
        /* On liste ce qu'on trouve dans le dossier */
        File file = new File(chemin);

        try {
            /* Pour chaque élément, on regarde si PDF ou dossier */
            for (File f : file.listFiles()) {
                /* Si PDF, alors on l'ajoute (le path) à la liste */
                if (f.getName().endsWith(".java")) {
                    this.incrementerAvecFichier(f.getAbsolutePath());
                }

                /* Si Dossier, appel récursif */
                if (f.isDirectory()) {
                    calculerNombreLignes(f.getAbsolutePath());
                }
            }
        } catch (NullPointerException ex) {
            //System.out.println("NullPointer : " + chemin);
        } finally {
            this.nombreDossierParcouru++;
            if(nombreDossierParcouru % 100 == 0) {
                System.out.print("%");
            }
            if(nombreDossierParcouru % 1000 == 0) {
                System.out.print("|->"+nombreDossierParcouru+"\n"+nombreDossierParcouru+"<-|");
            }
        }

    }

    /**
     * Méthode qui permet de choisir un dossier.
     */
    public void choisirDossier() {
        // Création de l'objet graphique.
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Affichage
        if (jfc.showDialog(jfc, "Choisir ce dossier") == JFileChooser.APPROVE_OPTION) {
            // Sauvegarde du dossier choisi.
            this.cheminDossier = jfc.getSelectedFile().toString();
            this.nomDossier = jfc.getSelectedFile().getName();
        }
    }

    public int getNombreLignes() {
        return nombreLignes;
    }

    public void setNombreLignes(int nombreLignes) {
        this.nombreLignes = nombreLignes;
    }

    public int getNombreLignesVides() {
        return nombreLignesVides;
    }

    public void setNombreLignesVides(int nombreLignesVides) {
        this.nombreLignesVides = nombreLignesVides;
    }

    public String getCheminDossier() {
        return cheminDossier;
    }

    public void setCheminDossier(String cheminDossier) {
        this.cheminDossier = cheminDossier;
    }

    /**
     * Incrémente le nombre de lignes automatiquement.
     *
     * @param absolutePath Chemin absolu du fichier.
     */
    public void incrementerAvecFichier(String absolutePath) {

        BufferedReader br;
        try {
            // Buffer de lecture
            br = new BufferedReader(new FileReader(absolutePath));

            // Boucle de lecture
            while (br.ready()) {
                // Lecture
                String line = br.readLine();

                // Dans tous les cas
                this.nombreLignes++;

                if (line.equals("")) {
                    this.nombreLignesVides++;
                }

                if (estCommentaire(line)) {
                    this.nombreLignesCommentaires++;
                }
            }

            // Fermeture
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Détermine si une ligne est un commentaire ou non.
     *
     * @param line la ligne
     * @return .
     */
    public boolean estCommentaire(String line) {
        String newline = line.trim();
        return newline.startsWith("/") || newline.startsWith("*");
    }

}
