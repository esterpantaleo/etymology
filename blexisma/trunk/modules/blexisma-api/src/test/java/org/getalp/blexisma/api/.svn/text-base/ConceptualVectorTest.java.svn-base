package org.getalp.blexisma.api;

import static org.junit.Assert.*;

import org.getalp.blexisma.api.ConceptualVectorRandomizer.UninitializedRandomizerException;
import org.junit.Before;
import org.junit.Test;


public class ConceptualVectorTest {

	private static final int DIM=2000;
	private static final int NORM = 2000000;
	
	private static final ConceptualVectorRandomizer randomizer = new DeviationBasedCVRandomizer(DIM, NORM);
	
    ConceptualVector cv1, cv2, cv3;

    @Before
    public void setUp() throws Exception {
        cv1 = randomizer.nextVector();
        cv2 = randomizer.nextVector();
        cv3 = randomizer.nextVector();
    }

    @Test
    public void testSumIsCommutative() {
        ConceptualVector res = cv1.sum(cv2);
        ConceptualVector res2 = cv2.sum(cv1);
        assertEquals(res, res2);
    }
    
    @Test
    public void testSerialization() {
        byte[] b = cv1.serializeArray();
        ConceptualVector res = new ConceptualVector(b, cv1.getCodeLength());
        assertEquals(cv1, res);
    }
    
    @Test
    public void testHexaSerialization() {
        String b = cv1.toStringHexa();
        ConceptualVector res = new ConceptualVector(b, cv1.getDimension(), cv1.getCodeLength());
        assertEquals(cv1, res);
    }
    
    @Test
    public void testCloning() {
        ConceptualVector v1 = (ConceptualVector) cv1.clone();
        ConceptualVector v2 = new ConceptualVector(cv1);
        assertEquals(cv1, v1);
        assertEquals(cv1, v2);
        assertEquals(v1, v2);
        v1.setElementAt(0, 0);
        v2.setElementAt(0, v2.getCodeLength());
        assertFalse("v1 and v2 should be different.", v1.equals(v2));
    }
    
    @Test
    public void testInit() {
        ConceptualVector v1 = (ConceptualVector) cv1.clone();
        v1.init();
        assertFalse("v1 should not be init.", v1.isInit());
        v1 = v1.scalar(10.);
        assertFalse("v1 should still not be init.", v1.isInit());
        double s = v1.scalarProduct(cv2);
        assertTrue("Scalar product of null vector should be 0", s == 0.);
    }
    
    @Test
    public void testRegularAngularDistance() {
        ConceptualVector v1 = (ConceptualVector) cv1.clone();
        ConceptualVector v2 = (ConceptualVector) cv2.clone();
        v1.normalise(); v2.normalise();
        assertEquals(v1.getAngularDistance(v2), cv1.getAngularDistance(cv2), 0.);
        assertEquals(v1.getRegularAngularDistance(v2), v1.getAngularDistance(v2), 0.00001);
    }
    
	@Test
	public void testAngularDistanceForEmptyVectors() {
		ConceptualVector cv = new ConceptualVector(DIM, NORM);
		ConceptualVector cv2 = new ConceptualVector(DIM, NORM);
		
		assertTrue("The angular distance between 2 empty vector is PI/2", cv.getAngularDistance(cv2) == Math.PI/2);
		assertTrue("The angular distance between an empty vector and itself is PI/2", cv.getAngularDistance(cv2) == Math.PI/2);
		
		
	}
	
	@Test
	public void testAngularDistance() throws UninitializedRandomizerException {
		ConceptualVector ecv = new ConceptualVector(DIM, NORM);
		ConceptualVector cv1 = randomizer.nextVector();
		ConceptualVector cv2 = randomizer.nextVector();

		assertTrue("The angular distance between 1 empty vector and 1 non empty vector is PI/2", ecv.getAngularDistance(cv1) == Math.PI/2);
		assertTrue("The angular distance should be commutative", cv2.getAngularDistance(cv1) == cv1.getAngularDistance(cv2));
		assertTrue("The angular distance between 2 randow vectors is less than PI/2", cv1.getAngularDistance(cv2) < Math.PI/2);
		// TODO: what should be the angular distance between a vector and itself ? 
		// double dist = cv1.getAngularDistance(cv1);
		// assertTrue("The angular distance between a vector and itself should be 0 (distance is " + dist + ")", dist == Math.PI/2);
	}
	
	@Test
	public void testAngularDistanceIdentity() throws UninitializedRandomizerException {
		ConceptualVector ecv = randomizer.nextVector();
		
		assertEquals(ecv.scalarProduct(ecv), Math.pow(ecv.getMagnitude(),2), 1.);
		assertEquals(1, ecv.getCosineSimilarity(ecv), 1E-10);
		double d = ecv.getAngularDistance(ecv);
		assertEquals(0., d, 1E-7);
		// TODO: what should be the angular distance between a vector and itself ? 
		// double dist = cv1.getAngularDistance(cv1);
		// assertTrue("The angular distance between a vector and itself should be 0 (distance is " + dist + ")", dist == Math.PI/2);
	}
	
	private String testVector = "7c0 b66 ae2 639 812 c1d 5d5 b9e 56f 6e6 21f b37 228 6c1 f72 1070 9cd 4b0 cc8 61d a50 4dc 1cb 5e9 9a0 af0 440 64c 268 a2b 23e 61a 34e 443 2d7 a4 737 85b 11c5 c94 4f7 656 b87 339 6e4 4a5 2db 887 edc 412 7ac 1046 470 7f4 683 3e4 814 572 341 60d ab7 611 56c 404 3f4 e2a 403 9b4 1168 859 f73 9a7 43c 4ba c6e be8 4be cc0 385 308 403 6ff 965 5e3 616 70d 775 fe9 ee2 875 4b4 b4e 629 b23 9ec 5a5 8a1 64d 88d 212 38f b35 a63 501 5f8 4bd 254 8fb 496 121f e98 bc1 34a 8f7 6b8 61d a34 a7a cc4 53e 6f3 117b a9a 53f 33f 5f4 518 46d c86 55d 24b 228 78e 709 626 5f2 7c6 906 7e7 6e6 161 2e6 332 c15 294 a52 569 2be 383 2c9 1e1 81d 753 a3b 76c 691 519 48d 289 934 60b a49 6e3 953 bbe 5fd a57 39a af2 b96 172 62a 7bb 454 469 54c 15a cb3 2d6 4a1 79e 49d 41a 2c2 11f6 826 732 543 57c eeb 88d 55f ae7 21d 467 42b 5ca 3bf 73a 27b 5dc 44a 126 284 75c 3a6 8d9 8da 8cb 73b 5c4 1e7 20b 656 24e 79f c39 869 129 50a 6cc 46e 4e6 3ec c09 52c 408 1e0 990 61f 7f9 9c8 444 d99 390 509 217 c7e e58 9e2 ebe 648 d36 310 145 4c4 619 61c 53b f77 558 900 4ce 4e4 c9c 286 476 7a0 678 8b1 6fb 7c1 3ce 43e 650 52f 707 3d7 2a8 3e4 690 f6e b1a afb 55b 12aa 31a 3e6 7fb afc 4c3 6d3 341 fe 6fd 291 cb1 81a 611 49c bc8 61f 457 294 52a 4c1 3f6 44e 1227 8a7 602 6c5 887 72c 2bc 120e 2e9 7f3 427 39b edd ef2 455 866 4b6 90f 2c1 43c 3e2 74c 4c9 421 9f5 185 995 35f 2e1 8be cd3 701 1ef 5b3 350 8e6 b3e 933 1a3 d9c 10e4 1074 93c 88a 921 3f0 99b 17b0 a91 d88 611 5af 548 be8 32b 72c 9cf 733 95a 470 122d 271 431 42f 8f8 46e acc 80e 1e7 2e2 bf9 273 803 62c a55 888 53f 291 ead 1ef 1412 2be 434 99c 6a6 599 ba1 e95 40c bb3 a16 a41 647 10e7 268 555 bd0 664 281 6b7 867 4a1 494 9a7 7ed 1195 d96 d18 dc2 269 5e9 49b d84 596 64c b3e 41b 3f4 695 ba7 507 576 53f 3b8 5e6 8af 481 a1c 5ab b10 8a4 5af 7ec 52e ca0 767 d45 7e5 62b cf9 5f0 504 3fe 180e 13c 93e 47a 7b5 295 83e 43a 785 41f 60d a27 5cb 2fb 3b5 4f4 b75 507 483 d99 2b3 beb 4c2 522 6d9 d1f f6c d34 272 61a 6ba 244 8ba 4d8 ddb 772 1416 5f1 f39 568 a30 fa5 c4b b35 186c 653 afa bbd a8d 291 168d 682 8f6 5c8 3af 584 2ed 42f a19 5d1 c27 6ad 677 1083 9f2 a05 e38 77a 3d0 3e9 603 489 9d9 494 9bd aa9 427 989 9ce 3d7 9eb 1693 bc2 1d8 998 8db e5e 47e bba 87c 4b1 2b4 af2 55d 2ed 5d8 532 6cc 12b2 275 9c1 734 987 45e 827 95e 65c 7e4 907 226 c8d 8cb a70 1fa 22b 274 83b fbc ae7 427 824 a4e 765 c85 cf0 998 978 15fb cd5 d4c 62a 953 1e7 3e8 519 51d 7fc 220 7fd 546 78f bfe 3c8 813 c1a f64 85f 250 907 b7c 4bd ab0 37d 2b9 326 b33 387 9b6 8b9 613 c7e 937 896 273 600 d21 d38 69e 2ba 632 86f 243 11c9 7f5 3f7 95e 47f 6aa f9b 943 413 e1 93b 89c 6ee a23 b17 5d9 85d 455 4b4 bb8 f42 618 5d5 cdd 5bd 61a bcc 2f6 367 68f 5e6 400 5d8 231 97a 799 7c9 1e3 692 2a3 798 1f8 bad 3a0 30b a1c 8fa 5cf 68f 927 54a ab5 388 4ca 471 a2e 1019 624 62a c87 31d 4f8 f94 98b 4f2 116c ded 1da 3c5 570 11f6 ad2 cc1 697 786 791 774 7d1 8cd 5b0 10f7 8c5 414 d0b 451 25b 6d1 26b 658 ab5 5b0 203 1d6 ed0 27e b22 1526 1053 390 49d 9cb 86e 638 d45 b8c 8d0 be7 2b7 37d 45f 56c 9c7 860 3f9 7f7 70c 1072 c6c 65c b52 8d8 479 4bd 57c 634 4dc 137a c08 3d1 f31 17f2 b4a 740 930 372 f68 5b4 650 571 78e 58c 543 635 823 949 7df 732 977 59d 4e9 5f0 3c6 7bc 413 b52 eb8 3a7 7a5 3d6 397 76d 814 403 1362 2a1 37b 559 550 83e 5f0 488 31e fad 84a e9 7b3 274 cb9 d88 1fb 37e 6ef 7ad 280 15f0 b49 678 e38 af0 893 36d b6d 515 531 100c 32d 5a0 941 701 680 616 845 82d 70d c14 3fd fea 7b6 c54 85b 5e4 71c 59c 388 7e1 1cc 102d 49d 342 21d 6d9 7ac 92f 312 bf3 cdb 80c 5f5 502 69e 648 519 239 5b9 613 d20 5dd 308 5f5 85d 820 6a0 91d 825 712 81b 90e b5d 2f6 673 753 af6 3df 3ed 39a b43 8aa 7dc 8c3 783 cad 14e4 492 2f0 d29 996 5fd c23 7a0 b44 d54 890 873 56d 712 dba 9c4 10cf 66c 911 5d0 cbb 71e a61 b32 944 a28 3ca 2cd ddc f65 343 1140 88f d07 5be db8 489 320 3b5 35a e2c 630 42b 86d ea9 4fe 3f3 762 318 425 ac2 7da 3d2 ca7 ad9 606 1012 39c 18ca 2ec 13f9 27b 4e5 9f7 9ee 78e 4e1 bda 478 84b 86e 8b6 4aa 44f bd0 c9a 3ee 328 89f 80c c75 3a0 87d 877 9c9 4ce 814 f2b 489 481 83c 36d bc7 a66 467 62b b67 731 4c8 6bc ea1 95b 60f da8 aab 8e3 cf9 8dc 3f0 de1 ce6 f16 97d 7ab 2d9 47b 37f 755 718 4b5 143e 757 552 149c 63c 385 62b 3c9 321 6d4 29b 728 680 e31 6ae a2e c18 ae5 892 8f6 3b5 481 145 774 93d 2b2 1f5 853 287 401 4d1 640 cab 2ed bd0 66c af3 59f 9c1 280 106f 92b 42f 603 7fc 2d2 30e 5b7 237 acd 744 4d8 557 d3d 12d5 985 852 19a 3d1 bf9 59b 4b4 398 147 3c6 c6b d85 e15 45b 6d3 3e3 e47 3c2 53b 6bc 6da de1 157 718 80f 8cd 745 2ad 867 ac1 c2c 298 31c 9fc b46 748 5e5 a2e 4d0 320 b7f 34e 847 925 4be d53 75c 468 40c 840 64a 4c0 698 b08 3fc a75 2b9 7eb bf7 382 3ae 637 684 6e3 598 d0f 64b 786 2b1 154c bfe 452 800 334 dd2 31e 3cc 529 2f8 8c1 467 a41 346 c02 721 901 af2 683 1132 a9d 7b9 ca3 f45 c7e de9 861 792 ee0 40c 1186 679 8bd 688 5ab 3f9 4cd e11 1014 9c6 649 a6f 7d9 d52 b25 2c0 a4f 326 467 70c 58e 22c 721 7f5 532 1193 3ef 53a a04 586 4bd 84a 661 d8a 4ee b0d 1085 bbd 54f 5a6 8d9 73a cb1 785 513 b0c 2dd 619 9f6 e3e 254 62a 880 4be 479 151 70c be1 933 6fd 13fd fc2 484 662 448 39a b2c 3b3 635 66d 579 997 b15 f60 25c df3 46a cf5 ba1 7bc 9dd 115c 87a 87d ba1 a49 cf9 ca1 4e5 b0f 805 23b 2df 893 982 ab2 2cd 3bd 9d7 524 724 38e c33 1dd bbf 864 7ce 63c e6c b0c 63f 741 b82 e32 9be 4a1 c51 7da 9f9 6d2 3ed 8f8 625 ca0 333 ae0 321 639 595 5a5 80b 7d4 cf5 f51 97d 613 71c 108a d1a 710 6b2 e3e 546 988 3dd 298 dac 3ee c3b 6b6 d13 9d5 6db 81c 2a8 79e 579 cd4 5be 640 745 7a9 69f 4bc 565 305 feb 3a5 4bd 600 9c9 b2d 788 55f 52c 30b 532 293 354 4a9 216 ac0 151f 5d5 46f 6fa c28 2a2 a44 98c fcc a6c ae7 7f3 183 d93 661 45b 89f 6b9 c2a 75e 3d1 c24 3e8 69e 951 aae 5de 2bb a1f 6cf 54d bb6 b0b 81a 700 c81 121a 517 b71 508 4b4 ff3 5f6 c13 1046 8fb 5d6 c18 51b 30e 711 32d 72b 5ee 8ca 77c 4da 767 451 51a 510 533 95d 2b1 919 e61 afb 69c ac0 31a 46d 842 b54 7b4 298 a56 aaa 93d b63 545 c43 8ae 3a0 6ec ff8 b0c 251 8cf 256 85a 1f0 1209 3b8 b2c 5d3 279 df7 b9f 427 678 4af 24d 9be 337 881 85b 564 2c3 1556 10f0 456 4fd 782 248 a68 a4c 618 827 b82 5d8 77a b21 694 e94 b2a 636 94f 81a 9b8 6a3 81b 71f 693 3e4 92b b79 753 65a 821 a5d 220 1a5 2ae 42e 467 73a 4f1 a50 546 51f 801 41c 82c 25f 871 3dd 62c 683 909 964 c70 74e dc3 ab3 24f 54e 10a9 7ad 560 57b 5da 3ca 577 ab8 a5b 9d7 491 80c be4 63e 44d 5fe 23d 514 472 846 386 925 7f1 4a5 b9d 5d2 575 1371 5aa 6ba 7e5 437 10c 6cd 3ad 21d c05 36f baf 429 51b 427 a7e 89b 610 525 1690 73d a0b a39 a94 3a6 64d 87a 659 f58 906 279 6cd 5b8 3b6 76f 100a 656 5dd 1fb f9d 3a4 53f 601 43e 27e 887 549 646 322 51d 70b 380 700 10ac 4e8 6ac 606 242 9a1 2d8 52c 7c1 b6b a24 728 5a3 4b8 875 88f 54d bb0 aa5 5b0 9de 576 cb6 94e d6a 69a 691 4ec 1b4 42e 813 862 d3c 244 115b 124e 42b 6f5 3e3 a38 48e de7 bf9 55f adb c57 1e3 cc1 83d 396 651 3ee 8bc 765 948 60d aa9 51d 597 b2d 69d 3b0 931 2e3 86b 6e4 dfd 5f7 505 417 435 4cb ce4 2e9 197 e8a 4bf 2f5 403 6d0 b93 eea 7a9 611 1e0 86c ca2 d35 91d 77c 5cb 247 7b8 544 dab 596 ad8 917 a23 633 b1e 1e7 983 468 2c2 5f2 2d9 4bd 529 893 8aa 56b a56 696 477 893 3ed a53 65e ef3 2ad 493 930 868 8a7 efd 1da 34b 2e9 c47 59f eec 87c 676 46a c6a c3c 48b 980 5b0 343 c2f 595 5ed 593 6ad 377 7b6 18a 4da 54c 59d 2d2 865 92e b26 1e3 a62 bc0 2f2 52b 2f3 7ee d95 afc 585 651 7ac 690 31a 241 9d4 a4b 2f8 6b2 2cb 479 54e 82c b5d 847 368 ce2 6dc 76f 14aa 983 c55 b16 729 8b9 1c4 2cc 724 285 a35 9d6 154 9f7 96a a8a 636 2a6 76c 4cc 182 a75 128a 85a 7cd 4ef d7d b42 f03 821 c87 362 5ab 82e 869 787 5b3 57d f03 6b4 82d 969 637 305 c94 633 65f 28d 4c3 4ec 9eb 8fe 40c 62a 85d ae6 f4f 6d1 62d 2fb 7bd 377 257 524 268 13fc 686 81f 2c6 9b9 786 4eb 9ff 4eb 59b 701 567 45b 681 90a 257 386 68a 39a 677 4ec 9ad 374 d3c 3a6 edf 6bc bd1 f7f bf8 cc3 113d ccf 7b5 fb3 531 465 885 2d4 8b6 8ae 6f1 1058 6ea e26 911 2b3 4ee ba6 296 35b 489 333 5b4 a16 437 37d ae5 309 a41 834 9fd 431 5eb 58a 4b0 3b1 208 4dc b31 8b1 933 80a 232 82f 4e9 870 127c f04 922 6a4 5e3 f1f 3bc 1b0 348 135 635 55f 511 657 5d9 2a5 8f9 176 617 32c 572 7de ac0 4ef 5d6 973 6fa 5de 699 444 83d b6b 7b4 53e 758 954 927 575 516 75a 12ee 8ce 830 72d e34 378 82d b74 f71 7f6 79f 34c ba4 576 53b 115 7b8 d82 1f0 47b 3e0 23a a17 11ef 4b2 e6d 846 bcf 535 5ed 7f3 64d a1e 9d6 69f 91a c9e 172 19d7 dbb 636 6b9 a9d 131 12c5 d57 a91 50a 3b5 43c 600 73e 683 c8f 9d7 a04 ab3";
	
	@Test
	public void testLoadFromString() {
		ConceptualVector v1 = new ConceptualVector(testVector,2000,32768);
		
		assertTrue("CV String loading work well.",testVector.equals(v1.toStringHexa()));
	}
	

	
	@Test
	public void testRamdomVector() throws UninitializedRandomizerException {
		ConceptualVector v1 = null;
		ConceptualVector v2 = null;
		int cpt = 0;
		
		for (int i=0; i<99; i++) {
			v1 = randomizer.nextVector();
			v2 = randomizer.nextVector();
			if (v1.getCosineSimilarity(v2)<0.9) cpt++;
		}
		
		assertTrue("Ramdomize work well.", cpt>40);
	}

}
