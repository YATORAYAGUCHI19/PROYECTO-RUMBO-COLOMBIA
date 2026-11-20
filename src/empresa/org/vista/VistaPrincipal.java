package empresa.org.vista;

import empresa.org.controlador.Controlador;
import empresa.org.modelo.ciudad.Ciudad;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VistaPrincipal extends JFrame {
    private static final Color VERDE = new Color(23, 75, 60);
    private static final Color TEXTO = new Color(35, 65, 53);
    private static final Color SUAVE = new Color(113, 129, 123);
    private static final Color CLARO = new Color(245, 248, 243);
    private final Controlador controlador;
    private final PanelAdministracion administracion;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel paginas = new JPanel(tarjetas);
    private final JComboBox<Ciudad> origen = new JComboBox<>();
    private final JComboBox<Ciudad> destino = new JComboBox<>();
    private final List<JComboBox<Ciudad>> escalas = new ArrayList<>();
    private final JPanel listaEscalas = new JPanel();
    private final JPanel contenidoCiudades = new JPanel();
    private final JLabel tiempo = new JLabel("—");
    private final JLabel estadoMapa = new JLabel("Selecciona una ruta");
    private final JTextArea mensaje = new JTextArea();
    private final JTextArea listaRuta = new JTextArea();
    private final MapaColombia mapa = new MapaColombia(estadoMapa);
    private final JToggleButton modoTour = new JToggleButton("Con escalas");
    private final JButton agregarEscala = botonSecundario("+ Agregar escala");
    private final JButton pausa = botonSecundario("Pausar");
    private JPanel areaPlan;

    public VistaPrincipal(Controlador controlador) {
        this.controlador = controlador;
        administracion = new PanelAdministracion(controlador);
        setTitle("Rumbo Colombia — El viaje empieza aquí");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(navegacion(), BorderLayout.NORTH);
        paginas.add(scroll(portada()), "inicio");
        areaPlan = planificador();
        paginas.add(scroll(areaPlan), "plan");
        JPanel paginaAdmin = vertical(CLARO);
        paginaAdmin.setBorder(new EmptyBorder(24, 35, 24, 35));
        JPanel formularioAdmin = administracion;
        formularioAdmin.setMaximumSize(new Dimension(950, 660));
        paginaAdmin.add(formularioAdmin);
        paginas.add(scroll(paginaAdmin), "admin");
        alinear(paginas);
        add(paginas, BorderLayout.CENTER);
        refrescarCiudades();
        origen.addActionListener(e -> limpiarResultado());
        destino.addActionListener(e -> limpiarResultado());
    }

    private JPanel navegacion() {
        JPanel barra = new JPanel(new BorderLayout()); barra.setBackground(Color.WHITE);
        barra.setBorder(new EmptyBorder(15, 40, 15, 40));
        JPanel marca = new JPanel(); marca.setOpaque(false); marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        JLabel nombre = texto("rumbo", 29, Font.BOLD, VERDE); nombre.setFont(new Font("SansSerif", Font.BOLD, 29));
        marca.add(nombre); marca.add(texto("C O L O M B I A", 10, Font.BOLD, VERDE)); barra.add(marca, BorderLayout.WEST);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 3)); acciones.setOpaque(false);
        JButton inicio = enlace("Inicio"), plan = enlace("Planifica tu viaje"), admin = enlace("Administrar ciudades");
        JButton crear = boton("Crear mi ruta");
        inicio.addActionListener(e -> tarjetas.show(paginas, "inicio"));
        plan.addActionListener(e -> abrirPlan()); crear.addActionListener(e -> abrirPlan());
        admin.addActionListener(e -> { administracion.actualizarCiudades(); tarjetas.show(paginas, "admin"); });
        acciones.add(inicio); acciones.add(plan); acciones.add(admin); acciones.add(crear); barra.add(acciones, BorderLayout.EAST);
        return barra;
    }

    private JPanel portada() {
        JPanel pagina = vertical(Color.WHITE); pagina.setBorder(new EmptyBorder(40, 55, 50, 55));
        JPanel hero = new JPanel(new GridLayout(1, 2, 45, 0)); hero.setOpaque(false); hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 475));
        JPanel mensaje = vertical(Color.WHITE); mensaje.add(texto("COLOMBIA, A TU MANERA", 12, Font.BOLD, new Color(111, 145, 96)));
        mensaje.add(Box.createVerticalStrut(20));
        JLabel titulo = new JLabel("<html>Un país.<br>Mil caminos<br><span style='color:#688263'>por descubrir.</span></html>");
        titulo.setFont(new Font("Georgia", Font.PLAIN, 57)); titulo.setForeground(VERDE); mensaje.add(titulo);
        mensaje.add(Box.createVerticalStrut(20)); mensaje.add(parrafo("Descubre ciudades de Colombia y planea tu ruta."));
        mensaje.add(Box.createVerticalStrut(25));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); botones.setOpaque(false);
        JButton plan = boton("Planificar mi viaje"); plan.addActionListener(e -> abrirPlan()); botones.add(plan);
        botones.setAlignmentX(LEFT_ALIGNMENT); mensaje.add(botones);
        hero.add(mensaje); hero.add(new Foto()); pagina.add(hero); pagina.add(Box.createVerticalStrut(30));
        pagina.add(Box.createVerticalStrut(25)); JLabel pregunta = new JLabel("Explora destinos"); pregunta.setForeground(VERDE); pregunta.setFont(new Font("Georgia", Font.PLAIN, 32)); pagina.add(pregunta);
        pagina.add(Box.createVerticalStrut(20)); JPanel destinos = new JPanel(new GridLayout(1, 3, 18, 0)); destinos.setOpaque(false); destinos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        destinos.add(destinoPortada("Pereira", "El Eje Cafetero", "Una pausa con aroma a café.", new Color(228, 239, 219)));
        destinos.add(destinoPortada("Cartagena", "El Caribe", "Historias junto al mar.", new Color(250, 231, 210)));
        destinos.add(destinoPortada("Bogotá", "Los Andes", "Cultura y nuevas historias.", new Color(223, 234, 232)));
        pagina.add(destinos); pagina.add(Box.createVerticalStrut(30));
        return pagina;
    }

    private JPanel destinoPortada(String ciudad, String region, String descripcion, Color color) {
        JPanel panel = vertical(color); panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        panel.add(texto(region.toUpperCase(), 11, Font.BOLD, VERDE)); panel.add(Box.createVerticalStrut(12));
        panel.add(texto(ciudad, 22, Font.BOLD, VERDE)); panel.add(texto(descripcion, 13, Font.PLAIN, SUAVE));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            abrirPlan(); seleccionar(destino, ciudad); seleccionar(origen, ciudad.equals("Bogotá") ? "Pereira" : "Bogotá");
        }}); return panel;
    }

    private JPanel planificador() {
        JPanel pagina = vertical(Color.WHITE); pagina.setBorder(new EmptyBorder(32, 45, 50, 45));
        JLabel titulo = new JLabel("Tu viaje, a tu manera."); titulo.setFont(new Font("Georgia", Font.PLAIN, 38)); titulo.setForeground(VERDE); pagina.add(titulo);
        pagina.add(parrafo("Elige dónde empezar, dónde llegar y qué descubrir en el camino.")); pagina.add(Box.createVerticalStrut(22));
        JPanel cuerpo = new JPanel(new BorderLayout(18, 0)); cuerpo.setOpaque(false);
        cuerpo.add(panelRuta(), BorderLayout.WEST); cuerpo.add(panelMapa(), BorderLayout.CENTER);
        cuerpo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 555)); pagina.add(cuerpo); pagina.add(Box.createVerticalStrut(36));
        JLabel tituloCiudades = new JLabel("Descubre tus ciudades"); tituloCiudades.setFont(new Font("Georgia", Font.PLAIN, 32)); tituloCiudades.setForeground(VERDE); pagina.add(tituloCiudades);
        pagina.add(Box.createVerticalStrut(18)); contenidoCiudades.setLayout(new BoxLayout(contenidoCiudades, BoxLayout.X_AXIS)); contenidoCiudades.setOpaque(false);
        contenidoCiudades.add(parrafo("Calcula una ruta para consultar la información de las ciudades."));
        JScrollPane ciudadesScroll = new JScrollPane(contenidoCiudades, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ciudadesScroll.setBorder(null); ciudadesScroll.setBackground(Color.WHITE); ciudadesScroll.setAlignmentX(LEFT_ALIGNMENT);
        ciudadesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 570)); pagina.add(ciudadesScroll);
        return pagina;
    }

    private JPanel panelRuta() {
        JPanel panel = vertical(Color.WHITE); panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,230,223)), new EmptyBorder(22,22,22,22)));
        panel.setPreferredSize(new Dimension(320, 520)); panel.add(texto("Diseña tu recorrido", 18, Font.BOLD, VERDE)); panel.add(Box.createVerticalStrut(18));
        JPanel modos = new JPanel(new GridLayout(1,2,5,0)); modos.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        JToggleButton simple = new JToggleButton("Un destino", true); ButtonGroup grupo = new ButtonGroup(); grupo.add(simple); grupo.add(modoTour);
        simple.setMargin(new Insets(8, 4, 8, 4)); modoTour.setMargin(new Insets(8, 4, 8, 4)); modos.add(simple); modos.add(modoTour); panel.add(modos); panel.add(Box.createVerticalStrut(18));
        panel.add(texto("Origen", 13, Font.BOLD, TEXTO)); panel.add(Box.createVerticalStrut(5)); panel.add(origen);
        JButton intercambiar = enlace("↕   Intercambiar ciudades"); intercambiar.addActionListener(e -> { Object a=origen.getSelectedItem(); origen.setSelectedItem(destino.getSelectedItem()); destino.setSelectedItem(a); });
        panel.add(intercambiar);
        listaEscalas.setLayout(new BoxLayout(listaEscalas, BoxLayout.Y_AXIS)); listaEscalas.setOpaque(false); panel.add(listaEscalas);
        agregarEscala.setVisible(false); agregarEscala.addActionListener(e -> agregarEscala()); panel.add(agregarEscala);
        modoTour.addActionListener(e -> { actualizarEscalas(); limpiarResultado(); }); simple.addActionListener(e -> { actualizarEscalas(); limpiarResultado(); });
        panel.add(Box.createVerticalStrut(8)); panel.add(texto("Destino", 13, Font.BOLD, TEXTO)); panel.add(Box.createVerticalStrut(5)); panel.add(destino);
        for (JComboBox<Ciudad> combo : Arrays.asList(origen,destino)) combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.add(Box.createVerticalStrut(16)); JButton calcular = boton("Calcular ruta"); calcular.addActionListener(e -> calcular()); panel.add(calcular);
        mensaje.setForeground(new Color(165, 76, 49)); mensaje.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mensaje.setEditable(false); mensaje.setOpaque(false); mensaje.setLineWrap(true); mensaje.setWrapStyleWord(true);
        mensaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54)); panel.add(Box.createVerticalStrut(8)); panel.add(mensaje);
        listaRuta.setEditable(false); listaRuta.setOpaque(false); listaRuta.setLineWrap(true); listaRuta.setWrapStyleWord(true);
        listaRuta.setForeground(VERDE); listaRuta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        listaRuta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); panel.add(listaRuta);
        panel.add(Box.createVerticalGlue()); return panel;
    }

    private JPanel panelMapa() {
        JPanel marco = new JPanel(new BorderLayout()); marco.setBackground(new Color(235,245,240)); marco.setBorder(BorderFactory.createLineBorder(new Color(218,232,223)));
        JPanel superior = new JPanel(new BorderLayout()); superior.setOpaque(false); superior.setBorder(new EmptyBorder(20,22,8,22));
        JPanel titulo = vertical(new Color(235,245,240)); titulo.add(texto("TU PRÓXIMA RUTA", 11, Font.BOLD, SUAVE));
        JLabel nombre = new JLabel("Colombia te espera"); nombre.setFont(new Font("Georgia",Font.PLAIN,25)); nombre.setForeground(VERDE); titulo.add(nombre); superior.add(titulo, BorderLayout.WEST);
        JPanel lado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); lado.setOpaque(false); tiempo.setFont(new Font("SansSerif",Font.BOLD,22)); tiempo.setForeground(VERDE); tiempo.setPreferredSize(new Dimension(130, 34)); tiempo.setHorizontalAlignment(SwingConstants.RIGHT); lado.add(tiempo);
        JButton mas = botonSecundario("+"), menos = botonSecundario("−"), reiniciar = botonSecundario("Restablecer");
        mas.addActionListener(e -> mapa.acercar()); menos.addActionListener(e -> mapa.alejar()); reiniciar.addActionListener(e -> mapa.reiniciarZoom());
        lado.add(menos); lado.add(mas); lado.add(reiniciar); superior.add(lado,BorderLayout.EAST); marco.add(superior,BorderLayout.NORTH); mapa.setPreferredSize(new Dimension(600, 400)); marco.add(mapa,BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout()); pie.setBackground(Color.WHITE); pie.setBorder(new EmptyBorder(10,18,10,18));
        JPanel leyenda = vertical(Color.WHITE); leyenda.add(texto("●  Origen       ●  Escala       ●  Destino", 12, Font.PLAIN, SUAVE));
        leyenda.add(texto("Mapa esquemático: no muestra carreteras reales.", 11, Font.PLAIN, SUAVE)); pie.add(leyenda,BorderLayout.WEST);
        JPanel reproducir = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); reproducir.setOpaque(false); estadoMapa.setForeground(VERDE); reproducir.add(estadoMapa);
        pausa.addActionListener(e -> pausa.setText(mapa.pausar() ? "Continuar" : "Pausar")); JButton repetir = botonSecundario("Repetir"); repetir.addActionListener(e -> { mapa.repetir(); pausa.setText("Pausar"); });
        reproducir.add(pausa); reproducir.add(repetir); pie.add(reproducir, BorderLayout.EAST); marco.add(pie,BorderLayout.SOUTH); return marco;
    }

    private void agregarEscala() {
        if (escalas.size() >= 4) return;
        JComboBox<Ciudad> combo = new JComboBox<>(); for (Ciudad c : controlador.obtenerCiudades()) combo.addItem(c); combo.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        combo.addActionListener(e -> limpiarResultado());
        escalas.add(combo); actualizarEscalas();
        limpiarResultado();
    }
    private void actualizarEscalas() {
        listaEscalas.removeAll(); agregarEscala.setVisible(modoTour.isSelected());
        if (modoTour.isSelected()) {
            for (JComboBox<Ciudad> combo : escalas) {
                JPanel fila = new JPanel(new BorderLayout(5,0)); fila.setOpaque(false); fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
                fila.add(combo,BorderLayout.CENTER); JButton quitar=botonSecundario("×"); quitar.addActionListener(e -> { escalas.remove(combo); actualizarEscalas(); limpiarResultado(); }); fila.add(quitar,BorderLayout.EAST);
                listaEscalas.add(fila); listaEscalas.add(Box.createVerticalStrut(5));
            }
        }
        agregarEscala.setEnabled(escalas.size() < 4); listaEscalas.revalidate(); listaEscalas.repaint();
    }

    private void limpiarResultado() {
        tiempo.setText("—");
        listaRuta.setText("");
        mensaje.setText("");
        mapa.setRuta(new ArrayList<>());
        contenidoCiudades.removeAll();
        contenidoCiudades.add(parrafo("Calcula una ruta para ver la información de las ciudades."));
        contenidoCiudades.revalidate();
        contenidoCiudades.repaint();
    }

    private void calcular() {
        Ciudad inicio = (Ciudad)origen.getSelectedItem(), fin = (Ciudad)destino.getSelectedItem();
        if (inicio == null || fin == null) { mensaje.setText("Agrega ciudades en Administrar ciudades."); return; }
        List<Ciudad> paradas = new ArrayList<>(); paradas.add(inicio);
        if (modoTour.isSelected()) {
            if (escalas.isEmpty()) { mensaje.setText("Agrega al menos una escala."); return; }
            for (JComboBox<Ciudad> combo : escalas) { Ciudad c=(Ciudad)combo.getSelectedItem(); if(c!=null) paradas.add(c); }
        }
        paradas.add(fin); List<Ciudad> camino = new ArrayList<>(); double total=0;
        for (int i=0;i<paradas.size()-1;i++) {
            Ciudad a=paradas.get(i), b=paradas.get(i+1);
            if (a.equals(b)) { mensaje.setText("Dos paradas consecutivas deben ser distintas."); return; }
            List<Ciudad> tramo=controlador.buscarRuta(a.getNombre(),b.getNombre());
            if (tramo.isEmpty()) { mensaje.setText("No hay ruta: " + a + " → " + b); return; }
            total+=controlador.calcularTiempoRuta(a.getNombre(),b.getNombre());
            if (!camino.isEmpty()) tramo = tramo.subList(1,tramo.size()); camino.addAll(tramo);
        }
        mensaje.setText(" "); tiempo.setText(formato(total)); listaRuta.setText("Ruta: " + unir(camino)); mapa.setRuta(camino); mostrarCiudades(paradas); pausa.setText("Pausar");
    }

    private void mostrarCiudades(List<Ciudad> ciudades) {
        contenidoCiudades.removeAll();
        for (Ciudad ciudad : new LinkedHashSet<>(ciudades)) {
            JPanel tarjeta=vertical(Color.WHITE); tarjeta.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,230,223)),new EmptyBorder(20,20,20,20)));
            tarjeta.setPreferredSize(new Dimension(260, 480)); tarjeta.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));
            JLabel nombre = new JLabel(ciudad.getNombre()); nombre.setFont(new Font("Georgia",Font.PLAIN,28)); nombre.setForeground(VERDE); tarjeta.add(nombre);
            String general=ciudad.getInformacionGeneral(); tarjeta.add(parrafo(general==null || general.trim().isEmpty()?"Información general pendiente.":general));
            grupo(tarjeta,"Hoteles",ciudad.getHoteles()); grupo(tarjeta,"Restaurantes",ciudad.getRestaurantes()); grupo(tarjeta,"Sitios turísticos",ciudad.getSitiosTuristicos());
            grupo(tarjeta,"Platos típicos",ciudad.getPlatosTipicos()); grupo(tarjeta,"Centros comerciales",ciudad.getCentrosComerciales()); tarjeta.add(Box.createVerticalGlue());
            contenidoCiudades.add(tarjeta); contenidoCiudades.add(Box.createHorizontalStrut(14));
        }
        contenidoCiudades.revalidate(); contenidoCiudades.repaint(); areaPlan.revalidate();
    }
    private void grupo(JPanel panel,String titulo,List<?> datos) {
        panel.add(Box.createVerticalStrut(12)); panel.add(texto(titulo,14,Font.BOLD,VERDE));
        if(datos.isEmpty()) panel.add(parrafo("Pendiente de investigar.")); else for(Object dato:datos) panel.add(parrafo(dato.toString()));
    }
    private void abrirPlan() { refrescarCiudades(); tarjetas.show(paginas,"plan"); }
    private void refrescarCiudades() {
        List<Ciudad> ciudades=controlador.obtenerCiudades();
        List<JComboBox<Ciudad>> combos=new ArrayList<>(Arrays.asList(origen,destino)); combos.addAll(escalas);
        for(JComboBox<Ciudad> combo:combos) { Object seleccionado=combo.getSelectedItem(); combo.removeAllItems(); for(Ciudad c:ciudades)combo.addItem(c); if(seleccionado!=null)combo.setSelectedItem(seleccionado); }
        if (ciudades.size()>0 && origen.getSelectedIndex()==0 && destino.getSelectedIndex()==0) { seleccionar(origen,"Bogotá"); seleccionar(destino,"Pereira"); }
        mapa.setCiudades(ciudades);
    }
    private void seleccionar(JComboBox<Ciudad> combo,String nombre) { for(int i=0;i<combo.getItemCount();i++) if(combo.getItemAt(i).getNombre().equalsIgnoreCase(nombre)) { combo.setSelectedIndex(i); return; } }
    private String formato(double minutos) { int horas=(int)(minutos/60); double resto=minutos-horas*60; return horas+" h "+(resto==(int)resto?String.valueOf((int)resto):String.valueOf(resto))+" min"; }
    private String unir(List<Ciudad> ciudades) { StringBuilder texto=new StringBuilder(); for(Ciudad ciudad:ciudades) { if(texto.length()>0) texto.append(" → "); texto.append(ciudad.getNombre()); } return texto.toString(); }
    private static void alinear(Component componente) { if(componente instanceof JComponent) ((JComponent)componente).setAlignmentX(LEFT_ALIGNMENT); if(componente instanceof Container) for(Component hijo:((Container)componente).getComponents()) alinear(hijo); }
    private static JPanel vertical(Color fondo) { JPanel p=new JPanel(); p.setBackground(fondo); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); return p; }
    private static JLabel texto(String s,int size,int estilo,Color color) { JLabel l=new JLabel(s); l.setFont(new Font("SansSerif",estilo,size)); l.setForeground(color); l.setAlignmentX(LEFT_ALIGNMENT); return l; }
    private static JLabel parrafo(String s) { JLabel l=new JLabel("<html><div style='width:290px;line-height:1.5'>"+escapar(s)+"</div></html>"); l.setFont(new Font("SansSerif",Font.PLAIN,14)); l.setForeground(SUAVE); l.setAlignmentX(LEFT_ALIGNMENT); return l; }
    private static String escapar(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
    private static JButton boton(String s) { JButton b = new BotonPrincipal(s); b.setAlignmentX(LEFT_ALIGNMENT); return b; }
    private static JButton enlace(String s) { JButton b=new JButton(s); b.setFont(new Font("SansSerif", Font.PLAIN, 14)); b.setForeground(VERDE); b.setBorder(new EmptyBorder(8,10,8,10)); b.setContentAreaFilled(false); b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setAlignmentX(LEFT_ALIGNMENT); return b; }
    private static JButton botonSecundario(String s) { JButton b=new JButton(s); b.setBackground(Color.WHITE); b.setForeground(VERDE); b.setFocusPainted(false); b.setAlignmentX(LEFT_ALIGNMENT); return b; }
    private static JScrollPane scroll(Component c) { JScrollPane s=new JScrollPane(c); s.setBorder(null); s.getVerticalScrollBar().setUnitIncrement(20); return s; }

    private static class Foto extends JPanel {
        private Image imagen;
        Foto() { java.net.URL url=getClass().getResource("/empresa/org/vista/cocora.jpg"); if(url!=null) imagen=new ImageIcon(url).getImage(); setOpaque(false); }
        @Override protected void paintComponent(Graphics gr) {
            super.paintComponent(gr); Graphics2D g=(Graphics2D)gr.create(); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.clip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),90,90));
            if(imagen!=null) { double escala=Math.max((double)getWidth()/imagen.getWidth(this),(double)getHeight()/imagen.getHeight(this)); int w=(int)(imagen.getWidth(this)*escala), h=(int)(imagen.getHeight(this)*escala); g.drawImage(imagen,(getWidth()-w)/2,(getHeight()-h)/2,w,h,this); }
            else { g.setColor(new Color(169,197,169)); g.fillRect(0,0,getWidth(),getHeight()); }
            g.setColor(new Color(0,0,0,115)); g.fillRoundRect(22,22,170,50,14,14); g.setColor(Color.WHITE); g.setFont(new Font("SansSerif",Font.BOLD,13)); g.drawString("Valle del Cocora",35,44); g.drawString("Quindío, Colombia",35,62); g.dispose();
        }
    }

    public static void configurarInterfaz() {
        Font normal = new Font("SansSerif", Font.PLAIN, 14);
        for (String clave : Arrays.asList("Label.font", "Button.font", "ToggleButton.font", "ComboBox.font", "TextField.font", "TextArea.font", "TabbedPane.font")) {
            UIManager.put(clave, normal);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); configurarInterfaz(); new VistaPrincipal(new Controlador()).setVisible(true); }
            catch(Exception e) { JOptionPane.showMessageDialog(null,"No se pudo abrir la aplicación: "+e.getMessage(),"Rumbo Colombia",JOptionPane.ERROR_MESSAGE); } });
    }
}
