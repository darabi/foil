(asdf:defsystem #:foil-oracle-1.7-base
  :depends-on (#:foil)
  :serial t
  :components
  ((:module "lisp"
    :components((:module "src"
                 :components((:file "foil")
                             (:module "oracle"
                              :components((:module "1.7"
                                           :components((:file "java-lang")
                                                       (:file "java-util")))))))))))
