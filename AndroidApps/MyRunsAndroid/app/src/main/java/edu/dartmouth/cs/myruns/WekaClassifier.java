package edu.dartmouth.cs.myruns;

// Generated with Weka 3.6.12
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Feb 14 13:01:51 EST 2015



        class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N73623b890(i);
        return p;
    }
    static double N73623b890(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 92.16005) {
            p = WekaClassifier.N3632da8d1(i);
        } else if (((Double) i[0]).doubleValue() > 92.16005) {
            p = WekaClassifier.N3d02d6fc7(i);
        }
        return p;
    }
    static double N3632da8d1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 60.519322) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 60.519322) {
            p = WekaClassifier.N44aa7ac72(i);
        }
        return p;
    }
    static double N44aa7ac72(Object []i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 1;
        } else if (((Double) i[28]).doubleValue() <= 0.143587) {
            p = 1;
        } else if (((Double) i[28]).doubleValue() > 0.143587) {
            p = WekaClassifier.N1462c2093(i);
        }
        return p;
    }
    static double N1462c2093(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 3.05226) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() > 3.05226) {
            p = WekaClassifier.N5fb0e7534(i);
        }
        return p;
    }
    static double N5fb0e7534(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 0.500118) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() > 0.500118) {
            p = WekaClassifier.N57232b395(i);
        }
        return p;
    }
    static double N57232b395(Object []i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = 0;
        } else if (((Double) i[29]).doubleValue() <= 0.808457) {
            p = 0;
        } else if (((Double) i[29]).doubleValue() > 0.808457) {
            p = WekaClassifier.N45ead7e06(i);
        }
        return p;
    }
    static double N45ead7e06(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 9.393028) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 9.393028) {
            p = 0;
        }
        return p;
    }
    static double N3d02d6fc7(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 396.967021) {
            p = WekaClassifier.N57059af8(i);
        } else if (((Double) i[0]).doubleValue() > 396.967021) {
            p = 2;
        }
        return p;
    }
    static double N57059af8(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 16.749449) {
            p = WekaClassifier.N130ff2f39(i);
        } else if (((Double) i[5]).doubleValue() > 16.749449) {
            p = WekaClassifier.N4417115123(i);
        }
        return p;
    }
    static double N130ff2f39(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 1;
        } else if (((Double) i[17]).doubleValue() <= 3.590668) {
            p = WekaClassifier.N7c17394b10(i);
        } else if (((Double) i[17]).doubleValue() > 3.590668) {
            p = WekaClassifier.N6ed7586321(i);
        }
        return p;
    }
    static double N7c17394b10(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if (((Double) i[8]).doubleValue() <= 6.996773) {
            p = WekaClassifier.N58b2aa9211(i);
        } else if (((Double) i[8]).doubleValue() > 6.996773) {
            p = WekaClassifier.N3663212518(i);
        }
        return p;
    }
    static double N58b2aa9211(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 131.118892) {
            p = WekaClassifier.N39dc84bc12(i);
        } else if (((Double) i[0]).doubleValue() > 131.118892) {
            p = 1;
        }
        return p;
    }
    static double N39dc84bc12(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 7.440313) {
            p = WekaClassifier.N3c8e705113(i);
        } else if (((Double) i[6]).doubleValue() > 7.440313) {
            p = 2;
        }
        return p;
    }
    static double N3c8e705113(Object []i) {
        double p = Double.NaN;
        if (i[21] == null) {
            p = 1;
        } else if (((Double) i[21]).doubleValue() <= 0.445741) {
            p = 1;
        } else if (((Double) i[21]).doubleValue() > 0.445741) {
            p = WekaClassifier.N2b6ff4e614(i);
        }
        return p;
    }
    static double N2b6ff4e614(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 0;
        } else if (((Double) i[7]).doubleValue() <= 1.025459) {
            p = 0;
        } else if (((Double) i[7]).doubleValue() > 1.025459) {
            p = WekaClassifier.N43373e5115(i);
        }
        return p;
    }
    static double N43373e5115(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 11.4344) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 11.4344) {
            p = WekaClassifier.N5fdc5a5016(i);
        }
        return p;
    }
    static double N5fdc5a5016(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 5.2073) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() > 5.2073) {
            p = WekaClassifier.N7bad6b5a17(i);
        }
        return p;
    }
    static double N7bad6b5a17(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 38.974361) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 38.974361) {
            p = 0;
        }
        return p;
    }
    static double N3663212518(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() <= 2.453645) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() > 2.453645) {
            p = WekaClassifier.N2615200819(i);
        }
        return p;
    }
    static double N2615200819(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 17.340582) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 17.340582) {
            p = WekaClassifier.N34ea00c020(i);
        }
        return p;
    }
    static double N34ea00c020(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 34.680011) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 34.680011) {
            p = 1;
        }
        return p;
    }
    static double N6ed7586321(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() <= 4.119282) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() > 4.119282) {
            p = WekaClassifier.N7bec046e22(i);
        }
        return p;
    }
    static double N7bec046e22(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() <= 5.156928) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() > 5.156928) {
            p = 1;
        }
        return p;
    }
    static double N4417115123(Object []i) {
        double p = Double.NaN;
        if (i[32] == null) {
            p = 2;
        } else if (((Double) i[32]).doubleValue() <= 1.040772) {
            p = 2;
        } else if (((Double) i[32]).doubleValue() > 1.040772) {
            p = WekaClassifier.N4671ffc324(i);
        }
        return p;
    }
    static double N4671ffc324(Object []i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() <= 4.75395) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() > 4.75395) {
            p = WekaClassifier.N664e7f5625(i);
        }
        return p;
    }
    static double N664e7f5625(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 71.596354) {
            p = WekaClassifier.N2b8a1b9326(i);
        } else if (((Double) i[2]).doubleValue() > 71.596354) {
            p = 0;
        }
        return p;
    }
    static double N2b8a1b9326(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 2;
        } else if (((Double) i[7]).doubleValue() <= 8.028066) {
            p = 2;
        } else if (((Double) i[7]).doubleValue() > 8.028066) {
            p = 1;
        }
        return p;
    }
}

