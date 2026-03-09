# 🛡️ PROJE TEKNİK REHBERİ VE SUNUM STRATEJİSİ ("Savaş Planı")
**Eczane Stok Yönetim Sistemi (Pharmacy Stock Management System)**

*Bu döküman, proje grubumuzun final sunumuna eksiksiz hazırlanması ve jüri/hoca karşısında projenin teknik derinliğini en üst düzeyde, özgüvenle savunabilmesi için bir **"Savaş Planı"** olarak hazırlanmıştır. Lütfen sunum öncesi herkes bu dökümanı dikkatlice okuyup özümsesin.*

---

## 1. GENEL MİMARİ KARARLARIMIZ (Neden Böyle Yaptık?)

Sunumda en kritik an, hocanın "Neden bu teknolojileri seçtiniz?" sorusudur. Cevaplarımız net ve mühendislik yaklaşımına uygun olmalıdır:

*   **Neden Spring Boot?**  
    *Cevap:* "Hocam, modern endüstri standartlarını yakalamak istedik. Klasik Java projelerindeki karmaşık XML konfigürasyonlarıyla vakit kaybetmek yerine, Spring Boot'un otomatik konfigürasyon (Auto-Configuration) ve gömülü web sunucusu (Embedded Tomcat) yetenekleriyle direkt olarak iş mantığına (Business Logic) odaklanabildik. Hızlı ve ölçeklenebilir bir backend ayağa kaldırmamızı sağladı."
*   **Neden MySQL?**  
    *Cevap:* "Eczacılık uygulamaları gibi finansal ve stok verilerinin tutulduğu sistemlerde **veri bütünlüğü (Data Integrity)** ve **ilişkisel bütünlük (Referential Integrity)** esastır. MySQL, ACID (Atomicity, Consistency, Isolation, Durability) prensiplerini mükemmel sağlayan, endüstri standardı ilişkisel bir veritabanı olduğu için tercih ettik."
*   **Neden 7 Tablo? (Minimum sınırı neden aştık?)**  
    *Cevap:* "Ödevin veya projenin gereksinimlerini sadece geçiştirmek değil, **gerçek dünyadaki bir eczane otomasyonunun akışını tam olarak simüle etmek** istedik. *Drug, Category, User, Purchase, Sale, SaleItem, Expiry* tablolarıyla, sadece ürün kaydetme değil; kategori bazlı filtreleme, login yetkilendirmesi, stok girdisi (Purchase), detaylı sepet satışı (Sale, SaleItem) ve kritik önem taşıyan miyok takibi (Expiry) senaryolarını kurduk."

---

## 2. KATMANLARIN DERİNLEMESİNE İNCELENMESİ (Layered Architecture)

Projemizi Spagetti Kod'dan uzak tutmak için "Katmanlı Mimari" kullandık. Hoca hangi sınıfın ne işe yaradığını sorduğunda yanıtlarımız:

*   **ENTITY (Varlık Katmanı):** Veritabanındaki tablolarımızın Java dünyasındaki karşılığıdır.
    *   *Neden Getter/Setter kullandık?* "Nesne Yönelimli Programlamanın (OOP) en temel ilkelerinden biri olan **Encapsulation (Veri Kapsülleme/Gizleme)** ilkesini ihlal etmemek için. Alanlarımızı `private` tutup, kontrollü erişimi `public` getter/setter metodlarıyla sağladık."
*   **DAO / REPOSITORY (Veri Erişim Katmanı):** 
    *   *Neden uzun SQL'ler yazmadık da JPA kullandık?* "Spring Data JPA kullanarak veritabanı işlemlerini soyutladık (Abstraction). Kodumuzu belirli bir SQL dialektine bağımlı olmaktan kurtardık ve daha az kodla (Boilerplate kodları engelleyerek) maksimum veri erişim güvenliği sağladık."
*   **SERVICE (İş Mantığı Katmanı):**
    *   *Neden direkt Controller'da işlem yapmıyoruz?* "Eğer bir satış sırasında hastadan para düşülüp, stok güncellenecekse bu Controller'ın değil Service katmanının işidir. Kod tekrarını önlemek ve Controller katmanını 'yalnızca HTTP isteklerini karşılayan ince bir katman (Thin Controller)' olarak tutmak istedik."
*   **CONTROLLER (API / Sunum Katmanı):** Dış dünyaya (Frontend/Swing tarafımıza) açılan RESTful kapılarımızdır. JSON formatında veri alışverişi yönetir.

---

## 3. TASARIM DESENLERİ (Design Patterns) - SUNUMUN KALBİ 💖

Projemizi "amatör" seviyesinden "kıdemli (senior)" seviyesine taşıyan yer tam olarak burasıdır. Bu kısımları gururla anlatın!

1.  **Builder Pattern (DrugBuilder.java):**  
    *Sunum Cümlesi:* "Hocam, bir ilaç oluştururken 10-11 tane parametre (`barcode`, `name`, `costPrice` vs.) göndermek gerekiyor. Klasik Constructor (Kurucu Metot) yaklaşımıyla kodun okunabilirliği ölüyor ve parametre sıraları (örneğin iki ayrı `BigDecimal` değerin karıştırılması) hataya çok açık hale geliyordu. Biz de Builder deseni sayesinde parametreleri isimleriyle (`.name("Aspirin").costPrice(...)` şeklinde) adım adım ve akıcı (fluent) bir okumayla set edebileceğimiz, hataya yer bırakmayan yapıyı kurduk."

2.  **Factory Pattern (TransactionFactory.java):**  
    *Sunum Cümlesi:* "Hem 'Alış' hem de 'Satış' işlemleri sisteme bir rekor olarak girer ve ikisinin de ortak ihtiyaçları (örneğin işlem anındaki güncel tarih: `LocalDate.now()`) vardır. Nesne üretim kodlarını (iş mantığını kirletmesin diye) `TransactionFactory` adındaki fabrikamızda merkezileştirdik ve standartlaştırdık."

3.  **Singleton Pattern (AppLogger.java):**  
    *Sunum Cümlesi:* "Proje genelinde hata veya bilgi mesajlarını loglamak için her seferinde yeni bir obje üretmek büyük bir performans ve hafıza israfıdır. Biz de Singleton deseni ile sistem hayatta olduğu sürece hafızada **sadece tek bir Log referansı (instance)** barınmasını sağladık."

4.  **DAO (Data Access Object) Pattern:**  
    *Sunum Cümlesi:* "Sistemin geri kalanı (Business Logic), verilerin MySQL'den mi yoksa test ortamında bellekteki bir listeden mi geldiğini bilmez, bilmek zorunda da değildir. Veri erişim soyutlamasını DAO deseni vasıtasıyla %100 oranında başardık."

---

## 4. HOCADAN GELEBİLECEK "ZOR / TUZAK" SORULAR VE CEVAPLARI 🛡️

*   **Soru 1:** *"Neden bu kadar basit bir proje için Interfaceler, ayrı ayrı Servisler (UserService, DrugService) yazdınız? Hepsini tek sınıf altına toplayamaz mıydınız?"*  
    **Cevap (Öldürücü Vuruş):** "Toplardık hocam ama bu, Yazılım Mühendisliğinin en temel kuralı olan **S.O.L.I.D. prensiplerinin ilki olan 'Single Responsibility (Tek Sorumluluk)'** prensibini ihlal etmek olurdu. Sürdürülebilirlik ve modülerlik adına her varlığın iş mantığını kendi servisinde yönettik."

*   **Soru 2:** *"Lombok gibi harika bir kütüphane varken neden bu kadar fazla Getter ve Setter'ı manuel yazıp kodu uzattınız?"*  
    **Cevap:** "Haklısınız hocam, sektörde çok yaygın kullanılıyor. Ancak projeyi hedeflediğimiz Java 25 gibi son sürümlerde bazen uyumluluk (Annotation Processing) sorunları yaratabiliyor. Ayrıca **akademik bir proje geliştirdiğimiz için**, kodun en temel temellerini (OOP kurallarını) makinenin otomatik yazmasına bırakmak yerine, mimarinin iskeletini tamamen manuel kontrol etmek ve temiz, bağımlılıksız (dependency-free) bir çekirdek elde etmek istedik."

*   **Soru 3:** *"Spring Security kullanmadığınızı görüyorum. Güvenlik zafiyeti değil mi?"*  
    **Cevap:** "Gerçek dünyada kesinlikle bir zafiyettir, evet. Ancak bu projenin temel odağı; Katmanlı Mimari, Design Pattern'ler ve Stok Yonetim akışını tasarlamaktı. Gereksiz bir karmaşıklık yığıp (Overengineering) odağımızı dağıtmak yerine, düz-metin (plain-text) tabanlı sadeleştirmeye gidip, takım eforumuzu doğru algoritma inşasına ve ilişkisel veritabanına odaklamayı tercih ettik."

---

## 5. FRONTEND (SWING) VE BACKEND İLETİŞİMİ (ApiClient) 🌐

*Swing panellerimiz veritabanına neden doğrudan bağlanmıyor?*
"Masaüstü uygulamamızın veritabanına direkt bağlanması monolitik (eski moda) bir yaklaşımdır. Biz tamamen modern **İstemci-Sunucu (Client-Server)** yapısını kurduk. Backend'imiz güvenli bir sığınaktır. Swing arayüzümüz sadece bir istemcidir (Frontend)."

*   **ApiClient.java'nın Rolü:**  
    *Sunum Cümlesi:* "Swing UI'ın, bizim Spring Boot Backend'imiz ile iletişim kurmasını sağlayan tek ve standardize edilmiş HTTP köprüsüdür. Tüm REST çağrıları buradan çıkar ve dönen JSON bu sınıfta parse edilir."
*   **Snippet'lar ve SwingWorker:**  
    *Sunum Cümlesi:* "Event-driven (Olay güdümlü) programlama mantığı kullandık. Ancak UI donmalarının (Freezing) önüne geçmek adına api isteklerini EDT (Event Dispatch Thread) üzerinde değil, arka planda (Background Thread) `SwingWorker` ile asenkron şekilde yönettiğimizi lütfen dikkate alın hocam."

---

## 6. SONRAKİ ADIMLAR VE EKRAN (UI) ÇIKTILARIMIZ

Biz bu projede bir "Demo" veya "Prototipten" ziyade çalışan endüstriyel bir iskelet sunduk. Figma tarafında tasarladığımız ekranları şu kod parçalarıyla birleştiriyoruz:

1.  **Login Ekranı:** Username ve Password alır. Apiclient aracılığı ile `UserService`e gider, veritabanı eşleşmesiyle dashboard'a yönlendirir.
2.  **Dashboard (Ana Ekran):** Eczanenin kalbidir. Kategorilerin listelendiği (ComboBox ile API'den güncel çekilir), stok sayılarının görüldüğü ve kırmızı uyarıların belirdiği (Miyadı yaklaşan ürünler tablosu) modern, akıcı bir arayüz tasarımını baz aldık.
3.  **İlaç Formu:** Tüm ürün özellikleri girilir ve Factory+Builder design patternlerimiz vasıtası ile veritabanına zerk edilir.

> **Takıma Tavsiye:** Sunuma çıkarken teknik terimleri (RestAPI, HTTP GET/POST, JSON, JSON Parse, Dependency Injection vb.) ezberlemeyin; mantığını anlatın. Projenin devasa altyapısının arkasında tamamen "Mühendislik Standartlarının ve Prensiplerinin (Clean Code)" yattığını vurgulayın. Hepimize başarılar, bu iş bizde! 🚀
