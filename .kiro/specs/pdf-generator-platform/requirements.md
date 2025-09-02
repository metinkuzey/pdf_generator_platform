# Requirements Document

## Introduction

Bu proje, kullanıcıların dinamik PDF şablonları oluşturabileceği, bu şablonları veri tabanında saklayabileceği ve web servisleri aracılığıyla veri ile birleştirerek PDF dosyaları üretebileceği kapsamlı bir platform geliştirmeyi hedeflemektedir. Sistem, hesap ekstreleri, ödeme makbuzları, kredi kartı ekstreleri, sağlık sigortası teklifleri ve poliçeleri gibi çeşitli belge türlerini destekleyecek şekilde tasarlanacaktır.

## Requirements

### Requirement 1

**User Story:** Bir tasarımcı olarak, tablo yapısı, başlık bölümleri ve dinamik veri alanları içeren profesyonel PDF şablonları oluşturabileceğim bir drag-and-drop arayüzü istiyorum, böylece kredi kartı ekstreleri, sigorta poliçeleri gibi karmaşık belge türlerini tasarlayabileyim.

#### Acceptance Criteria

1. WHEN kullanıcı şablon editörünü açtığında THEN sistem drag-and-drop destekli tablo, metin kutusu, başlık ve imaj elementleri sunacaktır
2. WHEN kullanıcı tablo elementi eklediğinde THEN sistem dinamik satır/sütun ekleme ve stil düzenleme imkanı sağlayacaktır
3. WHEN kullanıcı başlık bölümü tasarladığında THEN sistem logo, şirket bilgileri ve tarih alanları için özel kontroller sunacaktır
4. WHEN kullanıcı veri alanları eklediğinde THEN sistem {{müşteri_adı}}, {{poliçe_no}} gibi placeholder formatını destekleyecektir
5. WHEN kullanıcı sayfa düzeni ayarladığında THEN sistem A4, Letter gibi standart boyutları ve margin ayarlarını sunacaktır
6. IF kullanıcı şablonu kaydederse THEN sistem şablonu JSON formatında meta verilerle birlikte veri tabanına kaydedecektir

### Requirement 2

**User Story:** Bir sistem yöneticisi olarak, şablonları güvenli ve performanslı bir veri tabanında saklamak istiyorum, böylece hızlı erişim ve yüksek kullanılabilirlik sağlayabileyim.

#### Acceptance Criteria

1. WHEN sistem şablon verilerini sakladığında THEN PostgreSQL veri tabanını kullanacaktır
2. WHEN şablon sorgulandığında THEN sistem 100ms altında yanıt verecektir
3. WHEN veri tabanı yedeklendiğinde THEN sistem otomatik yedekleme mekanizması çalışacaktır
4. IF veri tabanı bağlantısı kesilirse THEN sistem connection pooling ile otomatik yeniden bağlanacaktır
5. WHEN şablon meta verileri saklandığında THEN sistem JSON formatında esnek şema kullanacaktır

### Requirement 3

**User Story:** Bir geliştirici olarak, REST API aracılığıyla şablon ve veri göndererek PDF üretebileceğim bir web servisi istiyorum, böylece farklı uygulamalardan PDF oluşturabileceğim.

#### Acceptance Criteria

1. WHEN API'ye şablon ID'si ve veri gönderildiğinde THEN sistem PDF dosyası üretip döndürecektir
2. WHEN PDF üretim isteği alındığında THEN sistem 5 saniye içinde yanıt verecektir
3. WHEN hatalı veri gönderildiğinde THEN sistem açıklayıcı hata mesajları döndürecektir
4. IF API rate limit aşılırsa THEN sistem 429 status kodu ile uygun mesaj döndürecektir
5. WHEN PDF üretildiğinde THEN sistem işlem logunu kaydedecektir

### Requirement 4

**User Story:** Bir güvenlik uzmanı olarak, tüm sistem bileşenlerinin güncel güvenlik standartlarına uygun olmasını istiyorum, böylece veri güvenliği ve sistem bütünlüğü sağlanabilsin.

#### Acceptance Criteria

1. WHEN kullanıcı sisteme giriş yaptığında THEN JWT tabanlı kimlik doğrulama kullanılacaktır
2. WHEN API çağrısı yapıldığında THEN HTTPS protokolü zorunlu olacaktır
3. WHEN veri tabanına erişim sağlandığında THEN şifrelenmiş bağlantı kullanılacaktır
4. IF güvenlik açığı tespit edilirse THEN sistem otomatik güvenlik güncellemeleri alacaktır
5. WHEN hassas veriler loglandığında THEN sistem veri maskeleme uygulayacaktır

### Requirement 5

**User Story:** Bir proje yöneticisi olarak, React frontend ve Spring Boot backend teknolojilerini kullanmak istiyorum, böylece modern ve sürdürülebilir bir mimari elde edebileceğim.

#### Acceptance Criteria

1. WHEN frontend geliştirildiğinde THEN React 18+ ve TypeScript kullanılacaktır
2. WHEN backend geliştirildiğinde THEN Spring Boot 3+ ve Java 17+ kullanılacaktır
3. WHEN API dokümantasyonu oluşturulduğunda THEN OpenAPI/Swagger entegrasyonu olacaktır
4. IF bağımlılık güncellemesi gerekirse THEN sistem otomatik güvenlik yamaları alacaktır
5. WHEN kod kalitesi kontrol edildiğinde THEN SonarQube entegrasyonu çalışacaktır

### Requirement 6

**User Story:** Bir sistem yöneticisi olarak, kapsamlı raporlama, izleme, loglama ve bildirim servisleri istiyorum, böylece sistem performansını ve sağlığını takip edebileceğim.

#### Acceptance Criteria

1. WHEN sistem çalıştığında THEN Prometheus metrikleri toplanacaktır
2. WHEN hata oluştuğunda THEN sistem otomatik alert gönderecektir
3. WHEN loglar oluşturulduğunda THEN ELK Stack ile merkezi loglama yapılacaktır
4. IF sistem performansı düştüğünde THEN otomatik bildirim gönderilecektir
5. WHEN raporlar oluşturulduğunda THEN Grafana dashboard'ları kullanılacaktır

### Requirement 7

**User Story:** Bir DevOps mühendisi olarak, yüksek yük altında çalışabilen ölçeklenebilir bir sistem istiyorum, böylece artan taleplere otomatik olarak yanıt verebileceğim.

#### Acceptance Criteria

1. WHEN sistem yükü arttığında THEN Kubernetes otomatik ölçeklendirme çalışacaktır
2. WHEN PDF üretim talebi yoğunlaştığında THEN sistem queue-based işleme geçecektir
3. WHEN veri tabanı yükü arttığında THEN read replica'lar otomatik devreye girecektir
4. IF bir servis çöktüğünde THEN sistem otomatik failover yapacaktır
5. WHEN cache ihtiyacı oluştuğunda THEN Redis cluster kullanılacaktır

### Requirement 8

**User Story:** Bir lisans yöneticisi olarak, redundant yapıya uygun esnek lisanslama sistemi istiyorum, böylece müşteriler yedekli kurulumlar yapabilsin.

#### Acceptance Criteria

1. WHEN lisans kontrol edildiğinde THEN sistem node-based lisanslama kullanacaktır
2. WHEN yedekli kurulum yapıldığında THEN sistem cluster-aware lisans kontrolü yapacaktır
3. WHEN lisans süresi dolduğunda THEN sistem graceful degradation uygulayacaktır
4. IF lisans sunucusu erişilemezse THEN sistem offline grace period sağlayacaktır
5. WHEN lisans kullanımı raporlandığında THEN sistem detaylı kullanım metrikleri sunacaktır

### Requirement 9

**User Story:** Bir sistem mimarı olarak, tam redundant yapı desteği istiyorum, böylece sistem kesintisiz hizmet verebilsin.

#### Acceptance Criteria

1. WHEN ana sistem çöktüğünde THEN yedek sistem otomatik devreye girecektir
2. WHEN veri senkronizasyonu yapıldığında THEN sistem real-time replication kullanacaktır
3. WHEN load balancer çalıştığında THEN sistem health check'leri sürekli yapacaktır
4. IF network bölünmesi oluşursa THEN sistem split-brain koruması sağlayacaktır
5. WHEN disaster recovery gerektiğinde THEN sistem otomatik backup'tan restore edecektir

### Requirement 10

**User Story:** Bir kullanıcı olarak, farklı belge türleri (kredi kartı tahsilat belgesi, sağlık sigortası poliçesi, hesap ekstresi, ödeme makbuzu) için özelleştirilmiş şablonlar oluşturmak istiyorum, böylece sektörel standartlara uygun belgeler üretebileceğim.

#### Acceptance Criteria

1. WHEN kredi kartı belgesi şablonu seçildiğinde THEN sistem müşteri bilgileri, poliçe bilgileri, işlem bilgileri ve ödeme planı bölümlerini önerecektir
2. WHEN sağlık sigortası şablonu kullanıldığında THEN sistem poliçe detayları, prim bilgileri, taksit planı ve imza alanları için hazır layout sunacaktır
3. WHEN tablo yapısı oluşturulduğunda THEN sistem çoklu sayfa desteği ve sayfa başlıkları tekrarı sağlayacaktır
4. WHEN dinamik liste verileri eklendiğinde THEN sistem otomatik satır ekleme ve toplam hesaplama özelliği sunacaktır
5. WHEN şirket logosu ve QR kod eklendiğinde THEN sistem görsel elementlerin pozisyon ve boyut ayarlarını destekleyecektir
6. IF şablon paylaşıldığında THEN sistem yetkilendirme kontrolü yapacaktır
7. WHEN şablon arşivlendiğinde THEN sistem soft delete uygulayacaktır

### Requirement 11

**User Story:** Bir şablon tasarımcısı olarak, tablo içeriklerinin dinamik olarak genişleyebileceği ve çoklu sayfa desteği olan şablonlar oluşturmak istiyorum, böylece değişken uzunluktaki veri listelerini düzgün şekilde gösterebileceğim.

#### Acceptance Criteria

1. WHEN tablo elementi oluşturulduğunda THEN sistem dinamik satır ekleme ve sayfa geçişi özelliği sunacaktır
2. WHEN veri listesi sayfa sınırını aştığında THEN sistem otomatik sayfa kırılması ve başlık tekrarı yapacaktır
3. WHEN toplam hesaplamaları eklendiğinde THEN sistem SUM, COUNT, AVERAGE gibi fonksiyonları destekleyecektir
4. WHEN sayfa numaralandırması yapıldığında THEN sistem "Sayfa X/Y" formatını destekleyecektir
5. IF veri boş gelirse THEN sistem "Veri bulunamadı" gibi özel mesajları gösterecektir

### Requirement 12

**User Story:** Bir geliştirici olarak, şablon içerisindeki veri alanlarının formatlanması ve validasyonu için özel kurallar tanımlayabilmek istiyorum, böylece tarih, para birimi ve telefon numarası gibi alanları doğru formatta gösterebileceğim.

#### Acceptance Criteria

1. WHEN para birimi alanı tanımlandığında THEN sistem "25.237,00 TL" formatında gösterim yapacaktır
2. WHEN tarih alanı eklendiğinde THEN sistem "14/07/2025" ve "14/07/2025 - 13:17" formatlarını destekleyecektir
3. WHEN telefon numarası alanı kullanıldığında THEN sistem "(212) 212 21 11" formatında maskeleme yapacaktır
4. WHEN kimlik numarası gösterildiğinde THEN sistem "****6010" gibi kısmi maskeleme uygulayacaktır
5. IF veri formatı hatalıysa THEN sistem varsayılan format uygulayacak ve log kaydı tutacaktır