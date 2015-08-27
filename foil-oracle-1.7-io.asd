(asdf:defsystem #:foil-oracle-1.7-io
  :depends-on (#:foil #:foil-oracle-1.7-base)
  :serial t
  :components
  ((:module "lisp"
    :components((:module "src"
                 :components((:file "foil")
                             (:module "oracle"
                              :components((:module "1.7"
                                           :components((:file "java-io")))))))))))
